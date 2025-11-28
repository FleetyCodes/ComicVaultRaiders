import { Component, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, NgForm } from '@angular/forms';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
    selector: 'app-login',
    standalone: true,
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    imports: [CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule, MatProgressSpinnerModule],
})

export class LoginComponent {
    
    constructor(private router: Router, private userService: UserService, private dialog: MatDialog) { }
    
    protected isLoading = signal<boolean>(false);

    goBack() {
        this.router.navigate(['/']);
    }


    onSubmit(form: NgForm) {
        if (form.valid) {
            this.isLoading.set(true);
            this.userService.login(form.value, this.router, this.dialog);
            this.isLoading.set(false);
        }
    }
}
