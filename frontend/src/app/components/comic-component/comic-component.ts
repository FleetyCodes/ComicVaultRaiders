import { Component, Input, signal } from "@angular/core";
import { Comic } from "../../models/comic";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { UserComicsService } from "../../services/user.comic.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-comic',
  templateUrl: './comic-component.html',
  styleUrls: ['./comic-component.scss'],
  imports: [MatIconModule, MatButtonModule, CommonModule]
})
export class ComicComponent {

  constructor(private userComicsService: UserComicsService) { }

  @Input() comic!: Comic;
  
  protected comicAdded = signal<boolean>(false);

  addComic() {
    this.userComicsService.addUserComicApi(String(this.comic.id)).subscribe({
      next: (response) => {
        this.userComicsService.addComicObject(response);
        this.comicAdded.set(true);
      },
      error: (err) => {
        console.error('Error adding comic:', err);
      }
    });
  }

}
