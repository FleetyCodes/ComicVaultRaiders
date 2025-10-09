import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';


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

    private apiUrl = 'http://localhost:8080/v1/user';


    constructor(private http: HttpClient, private cookieService: CookieService) { }


    register(data: RegisterRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/reg`, data);
    }


    login(data: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
    }

    logOut() : Observable<any> {
        const data = { refreshToken: this.getToken() };
        return this.http.post(`${this.apiUrl}/logout`, data);
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
        if(isLeft === null || isLeft === 'true' || isLeft === ''){
            return true;
        }else{
            return false;
        }
        
    }

    setLeftSidedNavbar(isLeft: boolean) {
        const expires = new Date();
        expires.setFullYear(expires.getFullYear() + 1);
        this.cookieService.set('isLeftSidedNavbar', isLeft.toString(), expires, '/');
    }
}
