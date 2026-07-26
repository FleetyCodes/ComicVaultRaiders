import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from './user.service';
import { environment } from '../../environments/environment';


@Injectable({
    providedIn: 'root'
})

export class AdminService {

    constructor(private http: HttpClient, private userService: UserService) { }

    private apiUrl = environment.apiUrl + "v1/admin";

    scrapeComics(scrapeKeyword: String): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.post(`${this.apiUrl}/scrapeComics`, scrapeKeyword, { headers });
    }


}
