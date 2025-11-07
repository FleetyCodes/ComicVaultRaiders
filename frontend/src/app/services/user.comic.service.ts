import { Injectable, signal } from "@angular/core";
import { UserComic } from "../models/user-comic";

@Injectable({
    providedIn: 'root'
})
export class UserComicsService {

    protected userComics = signal<UserComic[]>([]);

    setComics(newComics: UserComic[]) {
        this.userComics.set(newComics);
    }

    addComic(comic: UserComic) {
        this.userComics.update(cs => [...cs, comic]);
    }

    removeComic(comicToRemove: UserComic) {
        this.userComics.update(cs => cs.filter(c => c.comic.id !== comicToRemove.comic.id));
    }

    getComics() {
        return this.userComics();
    }

    getComicCount() {
        return this.userComics().length;
  }
}