import { Component, Inject, Input } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'basic-dialog',
  standalone: true,
  templateUrl: './basic-dialog.html',
  styleUrls: ['./basic-dialog.scss'],
  imports: [MatButtonModule]
})
export class basicDialog {

    constructor(private dialogRef: MatDialogRef<basicDialog>, @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string }) {}

    @Input() title!: string;
    @Input() message!: string;

    closeBtnClick() {
        this.dialogRef.close();
    }
}