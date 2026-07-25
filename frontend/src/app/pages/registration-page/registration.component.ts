import { Component, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, NgForm } from '@angular/forms';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { Subject, switchMap, interval, map, takeWhile, tap, finalize } from 'rxjs';

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
    private errorTrigger$ = new Subject<void>();

    ngOnInit() {
        this.errorTrigger$
            .pipe(
                switchMap(() => {
                    this.showUserAlreadyExistError.set(true);
                    this.opacity.set(1);

                    return interval(50).pipe(
                        map(i => 1 - i * 0.02),
                        takeWhile(v => v >= 0),
                        tap(v => this.opacity.set(v)),
                        finalize(() => this.showUserAlreadyExistError.set(false))
                    );
                })
            )
            .subscribe();
    }

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
                        this.errorTrigger$.next();
                    }
                }
            });
        }
    }
    
}
