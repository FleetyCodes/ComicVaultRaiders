import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, NgForm } from '@angular/forms';
import { MatIconModule } from "@angular/material/icon";
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { HelloService } from '../../services/hello.service';
import { basicDialog } from '../../components/basic-dialog/basic-dialog';
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
    
    constructor(private router: Router, private userService: UserService, private helloService: HelloService,) { }

    private dialog = inject(MatDialog);
    protected isLoading = signal<boolean>(false);

    goBack() {
        this.router.navigate(['/']);
    }


    onSubmit(form: NgForm) {
        if (form.valid) {
            this.isLoading.set(true);
            this.userService.login(form.value).subscribe({
                next: (res: any) => {
                    this.userService.setToken(res.token);
                    this.helloService.getHello().subscribe({
                        //this.helloService.getAllComics().subscribe({
                        next: (response) => {
                            this.isLoading.set(false);
                            this.helloService.setHelloTestMessage(response);
                        },
                        error: (err) => {
                            this.isLoading.set(false);
                            console.error('Error:', err);
                            this.helloService.setHelloTestMessage('Could not load data, please try again later. ');
                        }
                    });
                    this.router.navigate(['/logged-in']);
                },
                error: (err: any) => {
                    console.error(err);
                    this.isLoading.set(false);
                    const dialogRef = this.dialog.open(basicDialog, {
                        data: {
                            title: 'Login failed',
                            message: 'User not found or incorrect password. Please try again.',
                        },
                    });
                }
            });
        }
    }
}
