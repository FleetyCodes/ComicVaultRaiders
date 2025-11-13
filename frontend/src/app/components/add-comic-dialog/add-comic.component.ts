import { Component, computed, Inject, OnInit, signal } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from "@angular/common";
import { FormsModule, NgForm } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { ComicService } from "../../services/comic.service";
import { UserComicsService } from "../../services/user.comic.service";
import { Comic } from "../../models/comic";

@Component({
  selector: 'setup-wizard',
  standalone: true,
  templateUrl: './add-comic.component.html',
  styleUrls: ['./add-comic.component.scss'],
  imports: [CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule,]
})
export class addComicComponent implements OnInit {


  constructor(private userComicsService: UserComicsService, private comicService: ComicService, private dialogRef: MatDialogRef<addComicComponent>, @Inject(MAT_DIALOG_DATA) public data: { step: number, comicParam: Comic, onAddComic?: () => void },) { }

  protected stepState = signal<number>(1);
  protected newComic = signal<Comic | null>(null);
  protected title = computed(() => `Add New Comic - Step ${this.stepState()}`);
  protected message = signal<string>('');

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
        console.error('Error adding comic:', err);
      }
    });
  }

  addComic() {
    this.stepState.update(step => step + 1);
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
          console.error('Error adding comic:', err);
        }
      });
    }
  }


  onSubmitForm1(form: NgForm) {
    if (form.valid) {
      const comic = {
        ...form.value,
        releaseDate: new Date(form.value.releaseDate).toISOString()
      };

      this.comicService.addComic(comic).subscribe({
        next: (res: any) => {
          this.newComic.set(res);
          this.stepState.update(step => step + 1);
        },
        error: (err: any) => {
          console.error(err);
        }
      });
    } else {
      console.warn('Form is invalid!');
    }
  }
}

