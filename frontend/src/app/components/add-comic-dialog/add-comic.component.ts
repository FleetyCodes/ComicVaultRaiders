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
import { QrScannerQuaggaComponent } from "../qr-scanner-quagga/qr-scanner-quagga.component";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { Subject, switchMap, interval, map, takeWhile, tap, finalize } from "rxjs";

@Component({
  selector: 'setup-wizard',
  standalone: true,
  templateUrl: './add-comic.component.html',
  styleUrls: ['./add-comic.component.scss'],
  imports: [ReactiveFormsModule, CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule, MatSelectModule, QrScannerQuaggaComponent, MatProgressSpinnerModule]
})
export class addComicComponent implements OnInit {

  constructor(private userComicsService: UserComicsService, private comicService: ComicService, private dialogRef: MatDialogRef<addComicComponent>, @Inject(MAT_DIALOG_DATA) public data: { step: number, comicParam: Comic, onAddComic?: () => void },) { }

  protected stepState = signal<number>(1);
  protected newComic = signal<Comic | null>(null);
  protected title = computed(() => `Add New Comic - Step ${this.stepState()}/${this.maxValue}`);
  protected message = signal<string>('');
  publishers = Object.values(ComicPublishersEnum);
  comicFormats = Object.values(ComicFormatsEnum);
  selectedPublisher?: string;
  selectedFormat?: string;
  ComicCreationStepEnum = ComicCreationStepEnum;
  private maxValue = Math.max(...Object.values(this.ComicCreationStepEnum).filter(v => typeof v === 'number')) as number;

  protected scannedBarcode = signal<string>("");
  manualBarcodeInput: string = "";
  protected showError = signal<boolean>(false);
  protected errMg = signal<string>("");
  protected opacity = signal<number>(0);

  protected isLoading = signal<boolean>(false);
  
  private errorTrigger$ = new Subject<void>();

  ngOnInit() {
    if (this.data.comicParam) {
      this.newComic.set(this.data.comicParam);
    }
    this.stepState.set(this.data.step);
    this.message.set('Next, you will add the Comic to the system.');

    this.errorTrigger$
            .pipe(
                switchMap(() => {
                    this.showError.set(true);
                    this.opacity.set(1);

                    return interval(50).pipe(
                        map(i => 1 - i * 0.02),
                        takeWhile(v => v >= 0),
                        tap(v => this.opacity.set(v)),
                        finalize(() => this.showError.set(false))
                    );
                })
            )
            .subscribe();
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
    });
  }

  addComic() {
    this.stepState.set(this.ComicCreationStepEnum.ADD_TO_COLLECTION);
    this.message.set('Share your thoughts about this comic in your collection!');
  }


  addUserComicApiCall(form: NgForm) {
    const comic = {
      ...form.value,
      wishlisted: false,
      comic: this.newComic()!,
    };
    if (comic && this.newComic()) {
      this.isLoading.set(true);
      this.userComicsService.addUserComicApi(String(this.newComic()?.id), comic).subscribe({
        next: (response) => {
          this.userComicsService.addComicObject(response);
          if (this.data.onAddComic) {
            this.data.onAddComic();
          }
          this.isLoading.set(false);
          this.dialogRef.close();
        },
      });
    }
  }

  setStepState(newState: number) {
    this.stepState.set(newState);
  }

  createComicApiCall() {
    if (this.form.valid) {
      this.isLoading.set(true);
      const dateValue = this.form.value.releaseDate;
      const newDate = toZonedDateTimeString(dateValue!);

      const comic = {
        ...this.form.value,
        id: null,
        title: this.form.value.title!,
        author: this.form.value.author!,
        illustrator: this.form.value.illustrator!,
        format: this.form.value.format!,
        issueNumber: this.form.value.issueNumber!,
        publisher: this.form.value.publisher!,
        releaseDate: newDate!,
        coverImgUrl: this.form.value.coverImgUrl!,
      };

      this.comicService.addComic(comic).subscribe({
        next: (res: any) => {
          this.newComic.set(res);
          this.stepState.set(this.ComicCreationStepEnum.ADD_TO_COLLECTION_OR_WISHLIST);
          this.isLoading.set(false);
        },
      });
    }
  }


  form = new FormGroup({
    title: new FormControl<string>('', { validators: [Validators.required] }),
    author: new FormControl<string>('', { validators: [Validators.required] }),
    illustrator: new FormControl<string>('', { validators: [] }),
    format: new FormControl<string>('', { validators: [Validators.required] }),
    issueNumber: new FormControl<number>(1, { validators: [Validators.min(1), Validators.required] }),
    publisher: new FormControl<string>('', { validators: [Validators.required] }),
    releaseDate: new FormControl<string>('', {validators: []}),
    coverImgUrl: new FormControl<string>('', { validators: [] }),
  });


  checkComicInfo() {
    if ((!this.manualBarcodeInput || this.manualBarcodeInput.trim() === "") && (this.scannedBarcode() === "" || !this.scannedBarcode())) {
      this.errMg.set("Scan or type a barcode to proceed");
      this.errorTrigger$.next();
      return;
    }

    let barcodeString: string;
    if (this.manualBarcodeInput && this.manualBarcodeInput.trim() !== "") {
      barcodeString = this.manualBarcodeInput.trim();
    } else {
      barcodeString = this.scannedBarcode();
    }
    this.comicService.getComicInfoByBarcode(barcodeString).subscribe({
      next: (res: Comic) => {
        if (res.title) {
          this.form.patchValue({
            title: res.title,
            author: res.author,
            illustrator: res.illustrator,
            format: res.format,
            issueNumber: res.issueNumber,
            publisher: res.publisher,
            coverImgUrl: res.coverImgUrl,
          });
          if (res.releaseDate) {
            const releaseDateStr = res.releaseDate.split('T')[0]; // '2013-01-01'
            this.form.patchValue({
              releaseDate: releaseDateStr,
            });
          }
        } 
        this.stepState.set(this.ComicCreationStepEnum.MANUAL_CREATE_STEP);
      },
      error: (err: any) => {
        this.errMg.set("Error - please try again later");
        this.errorTrigger$.next();
      }
    });
  }

  onBarcodeScanned(code: string) {
    this.scannedBarcode.set(code);
  }

    public isMobile(): boolean {
    return /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
  }

}


function toZonedDateTimeString(dateStr: string | null): string | null {
  if (!dateStr) return null;

  const datePattern = /^\d{4}-\d{2}-\d{2}$/;
  if (!datePattern.test(dateStr)) return null;

  const date = new Date(dateStr);

  return date.toISOString();
}


