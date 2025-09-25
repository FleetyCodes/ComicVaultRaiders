import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';


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


    constructor(private http: HttpClient) { }


    register(data: RegisterRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/reg`, data);
    }


    login(data: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
    }

    setToken(token: string) {
        localStorage.setItem('authToken', token);
    }

    getToken(): string | null {
        return localStorage.getItem('authToken');
    }
}
