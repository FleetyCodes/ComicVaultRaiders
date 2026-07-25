import { Component, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, NgForm } from '@angular/forms';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';
import { LoginRequest, UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { IdleService } from '../../services/idle.service';
import { basicDialog } from '../../components/basic-dialog/basic-dialog';

@Component({
    selector: 'app-login',
    standalone: true,
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    imports: [CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule, MatProgressSpinnerModule],
})

export class LoginComponent {
    
    constructor(private router: Router, private userService: UserService, private dialog: MatDialog, private idleService: IdleService) { }
    
    protected isLoading = signal<boolean>(false);

    goBack() {
        this.router.navigate(['/']);
    }


    onSubmit(form: NgForm) {
        if (form.valid) {
            this.isLoading.set(true);
            this.login(form.value, this.router, this.dialog);
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
