import { Component, inject, Input, signal } from "@angular/core";
import { Comic } from "../../models/comic";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { CommonModule } from "@angular/common";
import { MatDialog } from "@angular/material/dialog";
import { addComicComponent } from "../add-comic-dialog/add-comic.component";
import { AppComponent } from "../../screens/main-page/app.component";
import { ComicCreationStepEnum } from "../../models/comic.creation.step.enum";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-comic',
  templateUrl: './comic-component.html',
  styleUrls: ['./comic-component.scss'],
  imports: [MatIconModule, MatButtonModule, CommonModule]
})
export class ComicComponent {

  constructor(private appComp: AppComponent) { }

  @Input() comic!: Comic;
  
  private dialog = inject(MatDialog);
  protected comicAdded = signal<boolean>(false);
  ComicCreationStepEnum = ComicCreationStepEnum;

  addComic() {
    const dialogRef = this.dialog.open(addComicComponent, {
      disableClose: true,
      autoFocus: false,
      data: {
        step: this.ComicCreationStepEnum.ADD_TO_COLLECTION_OR_WISHLIST,
        comicParam: this.comic,
        onAddComic: () => {
          this.setComicAdded(true);
        }
      },
    });
  }

  setComicAdded(added: boolean) {
    this.comicAdded.set(added);
  }

}
