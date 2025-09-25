import { Component, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'login-dialog',
  standalone: true,
  template: `
    <h1 mat-dialog-title>Login</h1>
    <div mat-dialog-content>
      <p>create the form here </p>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="onNoClick()">Close</button>
    </div>
  `,
})
export class loginDialog {
  readonly dialogRef = inject(MatDialogRef<loginDialog>, { optional: true });

  onNoClick(): void {
    if (this.dialogRef) {
      this.dialogRef.close();
    }
  }
}
