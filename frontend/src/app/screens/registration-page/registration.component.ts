import { Component, signal } from '@angular/core';
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

    protected showUserAlreadyExistError = signal<boolean>(false);
    protected opacity = signal<number>(0);
    protected errMg = signal<string>("");

    goBack() {
        this.router.navigate(['/']);
    }

    onSubmit(form: NgForm) {
        if (form.valid) {
            this.userService.register(form.value).subscribe({
                next: (res: any) => {
                    this.router.navigate(['/login']);
                },
                error: (err: any) => {
                    if (err.status === 409) {
                        this.errMg.set("This username already exists!");
                        this.showUserAlreadyExistError.set(true);
                        this.opacity.set(1);

                        let step = 4.0;
                        const interval = setInterval(() => {
                            step -= 0.05;
                            this.opacity.set(Math.max(step, 0));
                            if (this.opacity() <= 0) {
                                clearInterval(interval);
                                this.showUserAlreadyExistError.set(false);
                            }
                        }, 50);
                    }
                }
            });
        } else {
            console.warn('Form is invalid!');
        }
    }
}
