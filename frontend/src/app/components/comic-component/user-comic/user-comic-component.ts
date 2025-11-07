import { Component, inject, Input } from '@angular/core';
import { UserComic } from '../../../models/user-comic';
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { basicDialog } from '../../basic-dialog/basic-dialog';
import { UserComicsService } from '../../../services/user.comic.service';


@Component({
  selector: 'app-user-comic',
  templateUrl: './user-comic-component.html',
  styleUrls: ['./user-comic-component.scss'],
  imports: [MatIconModule, MatButtonModule]
})
export class UserComicComponent {

  constructor(private userComicsService: UserComicsService, private userComicService: UserComicsService) { }

  @Input() userComic!: UserComic;
  
  private dialog = inject(MatDialog);
  
  removeComic() {
    const dialogRef = this.dialog.open(basicDialog, {
      data: {
        title: 'Are you sure you want to remove this comic from your profile?',
        message: 'The comic will no longer appear in your collection, but you can always add it again later.',
        isWarningPopup: true,
        onConfirm: () => this.confirmRemove()
      },
    });
  }

  confirmRemove(): any {
    this.userComicsService.removeUserComicApi(String(this.userComic.id)).subscribe({
      next: () => {
        this.userComicService.removeComicObject(this.userComic);
      },
      error: (err) => {
        console.error('Error removing comic:', err);
      }
    });
  }

}
