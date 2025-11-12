import { Component, inject, Input, signal } from "@angular/core";
import { Comic } from "../../models/comic";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { CommonModule } from "@angular/common";
import { MatDialog } from "@angular/material/dialog";
import { addComicComponent } from "../add-comic-dialog/add-comic.component";

@Component({
  selector: 'app-comic',
  templateUrl: './comic-component.html',
  styleUrls: ['./comic-component.scss'],
  imports: [MatIconModule, MatButtonModule, CommonModule]
})
export class ComicComponent {

  constructor() { }

  @Input() comic!: Comic;
  
  private dialog = inject(MatDialog);
  protected comicAdded = signal<boolean>(false);

  addComic() {
    const dialogRef = this.dialog.open(addComicComponent, {
      disableClose: true,
      autoFocus: false,
      data: {
        step: 2,
        comicParam: this.comic,
        onAddComic: () => this.setComicAdded(true),
      },
    });
  }

  setComicAdded(added: boolean) {
    this.comicAdded.set(added);
  }

}
