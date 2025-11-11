import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from "@angular/common";
import { FormsModule, NgForm } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { ComicService } from "../../services/comic.service";

@Component({
  selector: 'setup-wizard',
  standalone: true,
  templateUrl: './add-comic.component.html',
  styleUrls: ['./add-comic.component.scss'],
  imports: [CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule,
  ]
})
export class setupWizardComponent {

  constructor(private comicService: ComicService, private dialogRef: MatDialogRef<setupWizardComponent>, @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string; form: NgForm; onConfirm?: () => void; }) { }

  onCancelClick() {
    this.dialogRef.close();
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      const comic = {
        ...form.value,
        releaseDate: new Date(form.value.releaseDate).toISOString()
      };

      this.comicService.addComic(comic).subscribe({
        next: (res: any) => {
          //return comic id for step 2
          console.log('Comic added with ID:', res.id);
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

