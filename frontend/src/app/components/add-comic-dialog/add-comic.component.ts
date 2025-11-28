import { Component, computed, Inject, OnInit, signal } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from "@angular/common";
import { FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { ComicService } from "../../services/comic.service";
import { UserComicsService } from "../../services/user.comic.service";
import { Comic } from "../../models/comic";
import { ComicPublishersEnum } from "../../models/comic.publishers.enum";
import { ComicFormatsEnum } from "../../models/comic.formats.enum";
import { MatSelectModule } from '@angular/material/select';
import { ComicCreationStepEnum } from "../../models/comic.creation.step.enum";
import { QrScannerComponent } from "../../qr-scanner/qr-scanner.component";
import { format } from "path";
import { release } from "os";


@Component({
  selector: 'setup-wizard',
  standalone: true,
  templateUrl: './add-comic.component.html',
  styleUrls: ['./add-comic.component.scss'],
  imports: [ReactiveFormsModule, CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule, MatSelectModule, QrScannerComponent]
})
export class addComicComponent implements OnInit {

  constructor(private userComicsService: UserComicsService, private comicService: ComicService, private dialogRef: MatDialogRef<addComicComponent>, @Inject(MAT_DIALOG_DATA) public data: { step: number, comicParam: Comic, onAddComic?: () => void },) { }

  protected stepState = signal<number>(1);
  protected newComic = signal<Comic | null>(null);
  protected title = computed(() => `Add New Comic - Step ${this.stepState()}/${this.maxValue}`); // Dynamic title based on current step and max ste`);
  protected message = signal<string>('');
  publishers = Object.values(ComicPublishersEnum);
  comicFormats = Object.values(ComicFormatsEnum);
  selectedPublisher?: string;
  selectedFormat?: string;
  ComicCreationStepEnum = ComicCreationStepEnum;
  private maxValue = Math.max(...Object.values(this.ComicCreationStepEnum).filter(v => typeof v === 'number')) as number;


  ngOnInit() {
    if (this.data.comicParam) {
      this.newComic.set(this.data.comicParam);
    }
    this.stepState.set(this.data.step);
    this.message.set('Next, you will add the Comic to the system.');
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  addWishlist() {
    const userComic = {
      id: 0,
      artRate: 0,
      storyRate: 0,
      panelRate: 0,
      comic: this.newComic()!,
      wishlisted: true,
    };
    this.userComicsService.addUserComicApi(String(this.newComic()?.id), userComic).subscribe({
      next: (response) => {
        this.userComicsService.addWishlistedComicObject(response);
        if (this.data.onAddComic) {
          this.data.onAddComic();
        }
        this.dialogRef.close();
      },
      error: (err) => {
      }
    });
  }

  addComic() {
    this.stepState.update(this.ComicCreationStepEnum.ADD_TO_COLLECTION.valueOf);
    this.message.set('Share your thoughts about this comic in your collection!');
  }


  onSubmitForm3(form: NgForm) {
    const comic = {
      ...form.value,
      wishlisted: false,
      comic: this.newComic()!,
    };
    if (comic && this.newComic()) {
      this.userComicsService.addUserComicApi(String(this.newComic()?.id), comic).subscribe({
        next: (response) => {
          this.userComicsService.addComicObject(response);
          if (this.data.onAddComic) {
            this.data.onAddComic();
          }
          this.dialogRef.close();
        },
        error: (err) => {
        }
      });
    }
  }

  setStepState(newState: number) {
    this.stepState.set(newState);
  }

  onSubmitForm1() {
    if (this.form.valid) {
      const dateValue = this.form.value.releaseDate;
      const comic = {
        ...this.form.value,
        id: null,
        title: this.form.value.title!,
        author: this.form.value.author!,
        illustrator: this.form.value.illustrator!,
        format: this.form.value.format!,
        issueNumber: this.form.value.issueNumber!,
        publisher: this.form.value.publisher!,
        releaseDate: new Date(dateValue!).toISOString(),
        coverImgUrl: this.form.value.coverImgUrl!,
      };

      this.comicService.addComic(comic).subscribe({
        next: (res: any) => {
          this.newComic.set(res);
          this.stepState.update(this.ComicCreationStepEnum.ADD_TO_COLLECTION_OR_WISHLIST.valueOf);
        },
        error: (err: any) => {
          //TODO: if err, show error message popup
        }
      });
    } else {
      console.warn('Form is invalid!');
    }
  }


  form = new FormGroup({
    title: new FormControl<string>('', { validators: [Validators.required] }),
    author: new FormControl<string>('', { validators: [Validators.required] }),
    illustrator: new FormControl<string>('', { validators: [Validators.required] }),
    format: new FormControl<string>('', { validators: [Validators.required] }),
    issueNumber: new FormControl<number>(1, {validators: [Validators.min(1), Validators.required]}),
    publisher: new FormControl<string>('', { validators: [Validators.required] }),
    releaseDate: new FormControl<Date | null>(null, Validators.required),
    coverImgUrl: new FormControl<string>('', { validators: [Validators.required] }),
  });

  onBarcodeScanned(scannedBarcode: string) {
    this.comicService.getComicInfoByBarcode(scannedBarcode).subscribe({
      next: (res: Comic) => {
        if (res.title) {
          this.form.patchValue({
            title: res.title,
            author: res.author,
            illustrator: res.illustrator,
            //format: res.format,
            issueNumber: res.issueNumber,
            //publisher: res.publisher,
            releaseDate: new Date(res.releaseDate),
            coverImgUrl: res.coverImgUrl,
          });

          this.stepState.update(this.ComicCreationStepEnum.MANUAL_CREATE_STEP.valueOf);
        } else {
          //TODO: comic not found handling
        }
      },
      error: (err: any) => {
        //TODO: api error handling
      }
    });
  }


  


}

