import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, NgForm } from '@angular/forms';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-register',
    standalone: true,
    templateUrl: './registration.component.html',
    styleUrls: ['./registration.component.scss'],
    imports: [CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, MatIconModule],
})

export class RegisterComponent {

    constructor(private router: Router, private userService: UserService) { }

    goBack() {
        this.router.navigate(['/']);
    }

    onSubmit(form: NgForm) {
        if (form.valid) {
            this.userService.register(form.value).subscribe({
                next: (res: any) => {
                    this.router.navigate(['/login']);
                },
                error: (err: any) => console.error(err)
            });
        } else {
            console.warn('Form is invalid!');
        }


    }
}
