import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../services/user.service';
import { IdleService } from '../services/idle.service';
import { Router } from '@angular/router';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';


@Component({
  selector: 'login-dialog',
  standalone: true,
  templateUrl: './login-dialog.html',
  styleUrls: ['./login-dialog.scss'],
  imports: [CommonModule, MatButtonModule, MatProgressSpinnerModule, MatFormFieldModule, MatInputModule, FormsModule, MatIconModule]
})
export class loginDialog {

  readonly dialogRef = inject(MatDialogRef<loginDialog>, { optional: true });

  private userService = inject(UserService);
  private idleService = inject(IdleService);
  private router = inject(Router);
  protected isLoading = signal<boolean>(false); 

  onNoClick(): void {
    if (this.dialogRef) {
      this.dialogRef.close();
    }
  }

  cancelClick() {
    this.isLoading.set(true);
    this.userService.logOut();
    this.userService.clearToken();
    this.idleService.stopIdleTimer();
    this.isLoading.set(false);
    this.router.navigate(['/']);
    this.dialogRef?.close();
  }

  loginClick(form: NgForm) {
          if (form.valid) {
            //TODO: change to refresh token api call
            //TODO: add loading spinner
            //TODO: open login popup, after 2 unsuccessful tries redirect to login page
            var loginAttempts = 0;

            this.userService.login(form.value).subscribe({
                next: (res: any) => {
                    this.userService.setToken(res.token);
                    this.idleService.startIdleTimer();
                    this.dialogRef?.close();
                },
                error: (err: any) => console.error(err)
            });
        } else {
            //handle invalid auth
        }
    }
  
 
  
}


