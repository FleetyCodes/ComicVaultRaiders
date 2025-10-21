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
import { ComicComponent } from "../../components/comic-component/comic-component";
import { ComicService } from "../../services/comic.service";
import { Comic } from "../../models/comic";

@Component({
    selector: 'my-comics',
    templateUrl: './my-comics.html',
    styleUrls: ['./my-comics.scss'],
    standalone: true,
    imports: [
    FormsModule, MatFormFieldModule, MatInputModule,
    CommonModule,
    RouterModule, MatButtonModule,
    ComicComponent
],
})

export class MyComicsPageComponent implements OnInit {

    constructor(private helloService: HelloService, private userService: UserService, private comicService: ComicService) { }

    protected userComics = signal<UserComic[]>([]);
    protected allComics = signal<Comic[]>([]);
    protected hasComics = signal<boolean>(false);
    protected yourComicsTitle = signal<string>('You haven\'t added any comics yet');
    

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
                this.userComics.set(response);
                if(this.userComics().length > 0){
                    this.hasComics.set(true);
                    this.yourComicsTitle.set('Your Comics');
                }
            },
            error: (err) => {
                console.error('Error:', err);
            }
        });




        this.comicService.getAllComics().subscribe({
            next: (response: Comic[]) => {
                console.log(response);
                this.allComics.set(response);
            },
            error: (err) => {
                console.error('Error:', err);
            }
        });
    }




}