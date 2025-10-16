import { Component, Input } from '@angular/core';
import { UserComic } from '../../models/user-comic';


@Component({
  selector: 'app-comic',
  templateUrl: './comic-component.html',
  styleUrls: ['./comic-component.scss']
})
export class ComicComponent {
  @Input() comic!: UserComic;
}
