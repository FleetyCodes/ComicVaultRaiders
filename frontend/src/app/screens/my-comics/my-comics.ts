import { CommonModule } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { RouterModule } from "@angular/router";
import { HelloService } from "../../services/hello.service";
import { UserService } from "../../services/user.service";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { UserComic } from "../../models/user-comic";
import { ComicService } from "../../services/comic.service";
import { Comic } from "../../models/comic";
import { UserComicComponent } from "../../components/comic-component/user-comic/user-comic-component";
import { ComicComponent } from "../../components/comic-component/comic-component";
import { UserComicsService } from "../../services/user.comic.service";

@Component({
    selector: 'my-comics',
    templateUrl: './my-comics.html',
    styleUrls: ['./my-comics.scss'],
    standalone: true,
    imports: [
        FormsModule, MatFormFieldModule, MatInputModule,
        CommonModule,
        RouterModule, MatButtonModule,
        UserComicComponent, ComicComponent
    ],
})

export class MyComicsPageComponent implements OnInit {

    constructor(private helloService: HelloService, private userService: UserService, private comicService: ComicService, protected userComicService: UserComicsService) { }

    protected allComicsExcludeUser = signal<Comic[]>([]);
    protected hasComics = signal<boolean>(false);
    protected yourComicsTitle = signal<string>('You haven\'t added any comics yet');

    protected searchedComics = signal<Comic[]>([]);
    protected totalElements = signal<number>(0);
    protected pageNumber = signal<number>(0);
    protected pageSize = signal<number>(0);
    protected searchKeyword = signal<string>("");
    
    protected showNotFoundError = signal<boolean>(false);
    protected searchErrMg = signal<string>("");
    protected opacity = signal<number>(0);


    ngOnInit() {
        this.helloService.getHello().subscribe({
            next: (response) => {
                this.helloService.setHelloTestMessage(response);
            },
            error: (err) => {
                console.error('Error:', err);
                this.helloService.setHelloTestMessage('Could not load data, please try again later. ');
            }
        });

        this.userService.getUserComics().subscribe({
            next: (response: UserComic[]) => {
                this.userComicService.setComicsObject(response);
                if (this.userComicService.getComicObjectCount() > 0) {
                    this.hasComics.set(true);
                    this.yourComicsTitle.set('Your Comics');
                }
            },
            error: (err) => {
                console.error('Error:', err);
            }
        });

        this.comicService.getAllComicsExcludeUser().subscribe({
            next: (response: Comic[]) => {
                this.allComicsExcludeUser.set(response);
            },
            error: (err) => {
                console.error('Error:', err);
            }
        });
    }


    searchComics() {
        if (this.searchKeyword().trim().length > 2) {
            this.comicService.getComicsBySearchable(this.pageNumber(), this.searchKeyword()).subscribe(response => {
                const ownedIds = new Set(this.userComicService.getComicsObject().map(u => u.comic.id));
                this.searchedComics.set(response.content.filter(c => !ownedIds.has(c.id)));
                if (this.searchedComics().length === 0) {
                    this.searchErrMg.set("Haven't found any comics with this parameter");
                    this.showNotFoundError.set(true);
                    this.opacity.set(1);
                    
                    let step = 4.0;
                    const interval = setInterval(() => {
                        step -= 0.05;
                        this.opacity.set(Math.max(step, 0));
                        if (this.opacity() <= 0) {
                        clearInterval(interval);
                        this.showNotFoundError.set(false);
                        }
                    }, 50);
                }
                this.totalElements.set(response.totalElements);
                this.pageSize.set(response.size);
            });
        }else {
            this.searchedComics.set([]);
            this.searchErrMg.set("Please enter at least 3 characters");
            this.showNotFoundError.set(true);
            this.opacity.set(1);

            let step = 4.0;
            const interval = setInterval(() => {
                step -= 0.05;
                this.opacity.set(Math.max(step, 0));
                if (this.opacity() <= 0) {
                    clearInterval(interval);
                    this.showNotFoundError.set(false);
                }
            }, 50);
        }
    }

    nextPage() {
        this.pageNumber.set(this.pageNumber() + 1);
        this.searchComics();
    }

    prevPage() {
        if (this.pageNumber() > 0) {
            this.pageNumber.set(this.pageNumber() - 1);
            this.searchComics();
        }
    }





}