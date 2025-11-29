import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comic } from '../models/comic';
import { UserService } from './user.service';
import { environment } from '../../environments/environment';



export interface PageResponse<T> {
  content: Comic[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
    providedIn: 'root'
})

export class ComicService {

    constructor(private http: HttpClient, private userService: UserService) { }

    private apiUrl = environment.apiUrl + "v1/comic";

    getAllComicsExcludeUser(): Observable<Comic[]> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.get<Comic[]>(`${this.apiUrl}/exceptUser/`, { headers });
    }

    getComicsBySearchable(pageNmbr: number, searchKeyword: string): Observable<PageResponse<Comic[]>> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });

        let params = new HttpParams()
            .set('page', pageNmbr)
            .set('searchBy', searchKeyword);

        return this.http.get<PageResponse<Comic[]>>(this.apiUrl,{ params, headers });
    }

    addComic(comic: Comic): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.post(`${this.apiUrl}`, comic, { headers });
    }

    getComicInfoByBarcode(barcode: string): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });

        let params = new HttpParams()
            .set('barcode', barcode)

        return this.http.get<PageResponse<Comic>>(`${this.apiUrl}/info/`, { params, headers });
    }
    
}
