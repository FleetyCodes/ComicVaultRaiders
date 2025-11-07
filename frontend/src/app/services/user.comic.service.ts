import { Injectable, signal } from "@angular/core";
import { UserComic } from "../models/user-comic";
import { UserService } from "./user.service";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UserComicsService {

    constructor(private http: HttpClient, private userService: UserService) { }

    private userBaseApipiUrl = 'http://localhost:8080/v1/user';


    removeUserComicApi(comicId: String): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.delete(`${this.userBaseApipiUrl}/comics/${comicId}`, { headers });
    }

    addUserComicApi(comicId: String): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.post(`${this.userBaseApipiUrl}/comic/${comicId}`, null, { headers });
    }

    protected userComics = signal<UserComic[]>([]);

    setComicsObject(newComics: UserComic[]) {
        this.userComics.set(newComics);
    }

    addComicObject(comic: UserComic) {
        this.userComics.update(cs => [...cs, comic]);
    }

    removeComicObject(comicToRemove: UserComic) {
        this.userComics.update(cs => cs.filter(c => c.comic.id !== comicToRemove.comic.id));
    }

    getComicsObject() {
        return this.userComics();
    }

    getComicObjectCount() {
        return this.userComics().length;
    }
}