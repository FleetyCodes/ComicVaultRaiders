import { CommonModule } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { RouterModule } from "@angular/router";
import { HelloService } from "../../services/hello.service";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { UserComic } from "../../models/user-comic";
import { UserComicComponent } from "../../components/comic-component/user-comic/user-comic-component";
import { UserComicsService } from "../../services/user.comic.service";


@Component({
    selector: 'wishlist-page',
    templateUrl: './wishlist.component.html',
    styleUrls: ['./wishlist.component.scss'],
    standalone: true,
    imports: [
        FormsModule, MatFormFieldModule, MatInputModule,
        CommonModule,
        RouterModule, MatButtonModule,
        UserComicComponent
    ],
})

export class WishlistedComicsPageComponent implements OnInit {

    constructor(private helloService: HelloService, protected userComicService: UserComicsService) { }

    
    protected hasComics = signal<boolean>(false);

    ngOnInit() {
        this.helloService.getHello().subscribe({
            next: (response) => {
                this.helloService.setHelloTestMessage(response);
            },
            error: () => {
                this.helloService.setHelloTestMessage('Could not load data, please try again later. ');
            }
        });

        this.userComicService.getUserComicsApi().subscribe({
            next: (response: UserComic[]) => {
                const filteredWishlistedComics = response.filter( uc => uc.wishlisted);
                this.userComicService.setWishlistedComicsObject(filteredWishlistedComics);
                if (this.userComicService.getWishlistedComicObjectCount() > 0) {
                    this.hasComics.set(true);
                }
            },
        });
    }
}