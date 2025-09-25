import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HelloService {

  constructor(private http: HttpClient) {}



  getHello(): Observable<string> {
    return this.http.get('http://localhost:8080/api/hello', { responseType: 'text' });
    //return this.http.get('https://localhost:8443/api/hello', { responseType: 'text' });
  }

  getAllComics(): Observable<string> {
    return this.http.get('http://localhost:8080/v1/comic', { responseType: 'text' });
  }
}
