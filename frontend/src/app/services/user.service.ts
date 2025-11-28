import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { basicDialog } from '../components/basic-dialog/basic-dialog';
import { IdleService } from './idle.service';


export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
}


@Injectable({
    providedIn: 'root'
})
export class UserService {

//    private apiUrl = 'http://localhost:8080/v1/user';
    private apiUrl = '/v1/user';

    constructor(private http: HttpClient, private cookieService: CookieService, private idleService: IdleService) { }

    
    register(data: RegisterRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/reg`, data);
    }


    loginApi(data: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data, {withCredentials: true});
    }

    login(data: LoginRequest, router: Router, dialog: MatDialog): void {
        this.loginApi(data).subscribe({
            next: (res: any) => {
                this.setToken(res.token);
                this.idleService.startIdleTimer();
                router.navigate(['/logged-in']);
            },
            error: (err: any) => {
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


    logOut(): Observable<any> {
        const token = this.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.post(`${this.apiUrl}/logout`, {}, {headers, withCredentials:true,  responseType: 'text'});
    }

    refreshJwtToken(): Observable<LoginResponse> {
        const data = { refreshToken: this.getToken() };
        return this.http.post<LoginResponse>(`${this.apiUrl}/refresh`, data, {withCredentials: true});
    }

    setToken(token: string) {
        const expires = new Date();
        expires.setHours(expires.getHours() + 2);
        this.cookieService.set('token', token, expires, '/');
    }

    getToken(): string | null {
        return this.cookieService.get('token');
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    clearToken() {
        this.cookieService.delete('token', '/');
    }

    isLeftSidedNavbar(): boolean {
        const isLeft = this.cookieService.get('isLeftSidedNavbar');
        if (isLeft === null || isLeft === 'true' || isLeft === '') {
            return true;
        } else {
            return false;
        }
    }

    setLeftSidedNavbar(isLeft: boolean) {
        const expires = new Date();
        expires.setFullYear(expires.getFullYear() + 1);
        this.cookieService.set('isLeftSidedNavbar', isLeft.toString(), expires, '/');
    }
}
