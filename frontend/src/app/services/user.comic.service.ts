import { Injectable, signal } from "@angular/core";
import { UserComic } from "../models/user-comic";
import { UserService } from "./user.service";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { SpringPageResponse } from "../models/page.model";


@Injectable({
    providedIn: 'root'
})
export class UserComicsService {

    constructor(private http: HttpClient, private userService: UserService) { }

    private userBaseApipiUrl = environment.apiUrl + "v1/user";

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

    getFilteredUserComicsApi(page: number, size: number, sort: string, title: string, publisher: string[], format: string[], author: string, illustrator: string, wishlisted: boolean): Observable<SpringPageResponse<UserComic>> {
        const token = this.userService.getToken();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
        });

        let params = new HttpParams()
        .set('page', page)
        .set('size', size);

        let url = `${this.userBaseApipiUrl}/filteredComics?page=${page}&size=${size}`;
        if(sort!==undefined && sort!==null && sort != 'undefined,'){
            //url += `&sort=${sort}`;
        }else{
            sort = 'comic.title,asc';
            //url += `&sort=id`;
        }
        params = params.set('sort', sort);

        if(title!==undefined && title!==null && title.trim().length>0){
            //url += `&filter=${filter}`;
            params = params.set('title', title);
        }

        if(author!==undefined && author!==null && author.trim().length>0){
            //url += `&filter=${filter}`;
            params = params.set('author', author);
        }

        if(illustrator!==undefined && illustrator!==null && illustrator.trim().length>0){
            //url += `&filter=${filter}`;
            params = params.set('illustrator', illustrator);
        }

        if (publisher !== undefined && publisher !== null) {
            //url += `&filter=${filter}`;
            publisher.forEach(p => {
                params = params.append('publisher', p);
            });
        }
        if(format!==undefined && format!==null){
            //url += `&filter=${filter}`;
            format.forEach(p => {
                params = params.append('format', p);
            });
        }
        params = params.set('wishlisted', wishlisted);
        return this.http.get<SpringPageResponse<UserComic>>(url, { headers, params });
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

    getComicsObjectWithCovers() {
        return this.userComics().filter(uc => uc.comic.coverImgUrl !== null && uc.comic.coverImgUrl !== undefined && uc.comic.coverImgUrl.trim().length > 0);
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

    getWishlistedComicsWithCoverImgUrlObject() {
        return this.userWishlistedComics().filter(uc => uc.comic.coverImgUrl !== null && uc.comic.coverImgUrl !== undefined && uc.comic.coverImgUrl.trim().length > 0);
    }

    getWishlistedComicObjectCount() {
        return this.userWishlistedComics().length;
    }
}