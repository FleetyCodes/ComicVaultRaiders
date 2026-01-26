import { Component, OnInit, signal } from "@angular/core";
import { UserService } from "../../services/user.service";
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatOption, MatSelectModule } from "@angular/material/select";
import { RouterModule } from "@angular/router";
import { AdminService } from "../../services/admin.service";
import { Subject, switchMap, interval, map, takeWhile, tap, finalize } from "rxjs";

@Component({
    selector: 'settings-screen',
    standalone: true,
    templateUrl: './settings-screen.html',
    styleUrls: ['./settings-screen.scss'],
    imports: [FormsModule, MatFormFieldModule, MatInputModule,
        CommonModule,
        RouterModule, MatButtonModule,
        MatFormFieldModule, MatInputModule, MatSelectModule,
        MatProgressSpinnerModule],
    providers: [
        { provide: JWT_OPTIONS, useValue: {} },
        JwtHelperService
    ]
})

export class SettingsScreenComponent implements OnInit {

    constructor(private userService: UserService, private jwtHelper: JwtHelperService, private adminService: AdminService) { }

    protected isAdmin = signal<boolean>(false);
    protected scraperKeyword = signal<string>("");
    protected isLoading = signal<boolean>(false);
    protected showNotFoundError = signal<boolean>(false);
    protected searchErrMg = signal<string>("");
    private errorTrigger$ = new Subject<void>();
    protected opacity = signal<number>(0);

    ngOnInit(): void {
        console.log("Settings Screen Loaded");
        let token = this.userService.getToken();

        if (this.jwtHelper.decodeToken(token!).userRole === 'ADMIN') {
            this.isAdmin.set(true);
        } else {
            this.isAdmin.set(false);
        }

        this.errorTrigger$
            .pipe(
                switchMap(() => {
                    this.showNotFoundError.set(true);
                    this.opacity.set(1);

                    return interval(50).pipe(
                        map(i => 1 - i * 0.02),
                        takeWhile(v => v >= 0),
                        tap(v => this.opacity.set(v)),
                        finalize(() => this.showNotFoundError.set(false))
                    );
                })
            )
            .subscribe();
    }

    searchComics() {
        console.log(this.scraperKeyword());
        if (this.scraperKeyword().trim().length > 2) {
            this.isLoading.set(true);

            this.adminService.scrapeComics(this.scraperKeyword()).subscribe({
                next: (res: any) => {
                    this.isLoading.set(false);
                    this.searchErrMg.set("Scraping completed successfully:" + res);
                    this.errorTrigger$.next();
                },
                error: (err: any) => {
                    this.scraperKeyword.set("");
                    this.isLoading.set(false);
                    this.searchErrMg.set("Error during scraping:" + err);
                    this.errorTrigger$.next();
                }
            });
        } else {
            this.searchErrMg.set("Please enter at least 3 characters");
            this.errorTrigger$.next();
        }
    }


}

