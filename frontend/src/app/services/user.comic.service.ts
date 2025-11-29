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

//    private userBaseApipiUrl = 'http://localhost:8080/v1/user';
    private userBaseApipiUrl = '/v1/user';

    removeUserComicApi(comicId: String): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.delete(`${this.userBaseApipiUrl}/comics/${comicId}`, { headers });
    }

    addUserComicApi(comicId: String, usercomic: UserComic): Observable<any> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.post(`${this.userBaseApipiUrl}/comic/${comicId}`, usercomic, { headers });
    }

    

    getUserComicsApi(): Observable<UserComic[]> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });
        return this.http.get<UserComic[]>(`${this.userBaseApipiUrl}/comics`, { headers });
    }

    protected userComics = signal<UserComic[]>([]);
    protected userWishlistedComics = signal<UserComic[]>([]);

    //user collected comics methods
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

    //wishlisted comics methods
    setWishlistedComicsObject(newComics: UserComic[]) {
        this.userWishlistedComics.set(newComics);
    }

    addWishlistedComicObject(comic: UserComic) {
        this.userWishlistedComics.update(cs => [...cs, comic]);
    }

    removeWishlistedComicObject(comicToRemove: UserComic) {
        this.userWishlistedComics.update(cs => cs.filter(c => c.comic.id !== comicToRemove.comic.id));
    }

    getWishlistedComicsObject() {
        return this.userWishlistedComics();
    }

    getWishlistedComicObjectCount() {
        return this.userWishlistedComics().length;
    }
}