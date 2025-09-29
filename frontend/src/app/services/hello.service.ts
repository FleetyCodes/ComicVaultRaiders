import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from './user.service';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class HelloService {

  private platformId = inject(PLATFORM_ID);

  constructor(private http: HttpClient, private userService: UserService) {}

  helloMessage = signal('Loading...');
   
  
  setHelloTestMessage(newMessage: string) {
    this.helloMessage.set(newMessage);
  }


  getHello(): Observable<string> {
    if (!isPlatformBrowser(this.platformId)) {
      return new Observable<string>((observer) => {
        observer.next('Loading...');
        observer.complete();
      });
    }

    const token = this.userService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    return this.http.get('http://localhost:8080/api/hello', { responseType: 'text', headers });
  }

  getAllComics(): Observable<string> {
    return this.http.get('http://localhost:8080/v1/comic', { responseType: 'text' });
  }
}
