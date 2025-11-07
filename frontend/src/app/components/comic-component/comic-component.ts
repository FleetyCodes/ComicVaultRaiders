import { Component, Input } from "@angular/core";
import { Comic } from "../../models/comic";

@Component({
  selector: 'app-comic',
  templateUrl: './comic-component.html',
  styleUrls: ['./comic-component.scss']
})
export class ComicComponent {
  @Input() comic!: Comic;
}
