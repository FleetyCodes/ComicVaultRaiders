import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from "@angular/common";
import { NgForm } from "@angular/forms";

@Component({
  selector: 'setup-wizard',
  standalone: true,
  templateUrl: './setup-wizard.component.html',
  styleUrls: ['./setup-wizard.component.scss'],
  imports: [MatButtonModule, CommonModule]
})
export class setupWizardComponent {


  constructor(private dialogRef: MatDialogRef<setupWizardComponent>, @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string; form: NgForm; onConfirm?: () => void; }) { }

  /*
  onConfirmClick() {
    if (this.data.onConfirm) this.data.onConfirm();
    this.dialogRef.close();
  }

  closeBtnClick() {
    this.dialogRef.close();
  }
  */


  onNextClick() {
    
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  onBackClick() {
    
  }
}