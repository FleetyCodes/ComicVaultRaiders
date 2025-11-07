import { Component, Inject, Input, signal } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from "@angular/common";

@Component({
  selector: 'basic-dialog',
  standalone: true,
  templateUrl: './basic-dialog.html',
  styleUrls: ['./basic-dialog.scss'],
  imports: [MatButtonModule, CommonModule]
})
export class basicDialog {

  constructor(private dialogRef: MatDialogRef<basicDialog>, @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string; isWarningPopup: boolean; onConfirm?: () => void; }) { }

  onConfirmClick() {
    if (this.data.onConfirm) this.data.onConfirm();
    this.dialogRef.close();
  }

  closeBtnClick() {
    this.dialogRef.close();
  }
}