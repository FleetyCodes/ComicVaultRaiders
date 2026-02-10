import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { LoginRequest, UserService } from '../../services/user.service';
import { IdleService } from '../../services/idle.service';
import { Router } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { basicDialog } from '../basic-dialog/basic-dialog';


@Component({
  selector: 'login-dialog',
  standalone: true,
  templateUrl: './login-dialog.html',
  styleUrls: ['./login-dialog.scss'],
  imports: [CommonModule, MatButtonModule, MatProgressSpinnerModule, MatFormFieldModule, MatInputModule, FormsModule, MatIconModule]
})
export class loginDialog {

  readonly dialogRef = inject(MatDialogRef<loginDialog>, { optional: true });
  private dialog = inject(MatDialog);
  private userService = inject(UserService);
  private idleService = inject(IdleService);
  private router = inject(Router);
  protected isLoading = signal<boolean>(false);


  cancelClick() {
    this.isLoading.set(true);
    this.userService.logOut().subscribe();
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
      //var loginAttempts = 0;


      this.login(form.value, this.router, this.dialog);
      this.dialogRef?.close();
    } else {
      //handle invalid auth
    }
  }

   login(data: LoginRequest, router: Router, dialog: MatDialog): void {
          this.userService.loginApi(data).subscribe({
              next: (res: any) => {
                  this.userService.setToken(res.token);
                  this.isLoading.set(false);
                  this.idleService.startIdleTimer();
                  router.navigate(['/logged-in']);
              },
              error: (err: any) => {
                  this.isLoading.set(false);
                  const dialogRef = dialog.open(basicDialog, {
                      data: {
                          title: 'Login failed',
                          message: 'User not found or incorrect password. Please try again.',
                          isWarningPopup: false
                      },
                  });
              }
          });
      }
  
}