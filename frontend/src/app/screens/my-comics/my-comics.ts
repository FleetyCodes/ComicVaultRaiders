import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal, ViewChild } from "@angular/core";
import { RouterModule } from "@angular/router";
import { FormControl, FormGroup, FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { UserComic } from "../../models/user-comic";
import { ComicService } from "../../services/comic.service";
import { Comic } from "../../models/comic";
import { UserComicComponent } from "../../components/comic-component/user-comic/user-comic-component";
import { ComicComponent } from "../../components/comic-component/comic-component";
import { UserComicsService } from "../../services/user.comic.service";
import { MatDialog } from "@angular/material/dialog";
import { addComicComponent } from "../../components/add-comic-dialog/add-comic.component";
import { ComicCreationStepEnum } from "../../models/comic.creation.step.enum";
import { AppComponent } from "../main-page/app.component";
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from '@angular/material/sort';
import { ReactiveFormsModule } from '@angular/forms';
import { merge, debounceTime, startWith, switchMap } from "rxjs";
import { MatOption } from "@angular/material/select";
import { MatSelectModule } from '@angular/material/select';
import { ComicPublishersEnum } from "../../models/comic.publishers.enum";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { ComicFormatsEnum } from "../../models/comic.formats.enum";


@Component({
    selector: 'my-comics',
    templateUrl: './my-comics.html',
    styleUrls: ['./my-comics.scss'],
    standalone: true,
    imports: [
    FormsModule, MatFormFieldModule, MatInputModule,
    CommonModule,
    RouterModule, MatButtonModule,
    UserComicComponent, ComicComponent,
    MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule,
    MatOption, MatSelectModule,
    MatProgressSpinnerModule
],
})

export class MyComicsPageComponent implements OnInit {

    constructor(private appComp: AppComponent, private comicService: ComicService, protected userComicService: UserComicsService) { }

    protected allComicsExcludeUser = signal<Comic[]>([]);
    protected hasComics = signal<boolean>(false);

    protected searchedComics = signal<Comic[]>([]);
    protected totalElements = signal<number>(0);
    protected pageNumber = signal<number>(0);
    protected pageSize = signal<number>(0);
    protected searchKeyword = signal<string>("");

    protected showNotFoundError = signal<boolean>(false);
    protected searchErrMg = signal<string>("");
    protected opacity = signal<number>(0);

    private dialog = inject(MatDialog);
    protected gridComics = signal<UserComic[]>([]);

    dataSource = new MatTableDataSource<UserComic>([]);
    displayedColumns = ['comic.title', 'comic.author', 'comic.illustrator', 'comic.publisher', 'comic.format', 'comic.releaseDate', 'comic.issueNumber', 'artRate', 'storyRate', 'panelRate', 'wishlisted'];
    
    publishers = Object.values(ComicPublishersEnum);
    comicFormats = Object.values(ComicFormatsEnum);

    filterForm = new FormGroup({
        title : new FormControl(''),
        author : new FormControl(''),
        illustrator : new FormControl(''),
        publisher: new FormControl<string[]>([]),
        format: new FormControl<string[]>([]),
    });

    
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    ComicCreationStepEnum = ComicCreationStepEnum;

    ngOnInit() {
        this.userComicService.getUserComicsApi().subscribe({
            next: (response: UserComic[]) => {
                const filteredComicsWithoutWishlisted = response.filter( uc => !uc.wishlisted);
                const filteredWishlistedComics = response.filter( uc => uc.wishlisted);
                this.gridComics.set(filteredComicsWithoutWishlisted.filter(uc => uc.comic.coverImgUrl && uc.comic.coverImgUrl!==null && uc.comic.coverImgUrl.trim().length > 0));
                this.userComicService.setComicsObject(filteredComicsWithoutWishlisted);
                this.userComicService.setWishlistedComicsObject(filteredWishlistedComics);
                if (this.userComicService.getComicObjectCount() > 0) {
                    this.hasComics.set(true);
                }
            },
        });

        this.comicService.getAllComicsExcludeUser().subscribe({
            next: (response: Comic[]) => {
                this.allComicsExcludeUser.set(response);
            },
        });
    }

    ngAfterViewInit() {
        Promise.resolve().then(() => {
            this.sort.active = 'comic.title';
            this.sort.direction = 'asc';
        });

        merge(
            this.paginator.page,
            this.sort.sortChange,
            this.filterForm.valueChanges.pipe(debounceTime(300)),
        )
            .pipe(
                startWith({
                    pageIndex: 0,
                    pageSize: 10
                }),
                switchMap(() => {
                    const f = this.filterForm.value;

                    return this.userComicService.getFilteredUserComicsApi(
                        this.paginator.pageIndex,
                        this.paginator.pageSize,
                        `${this.sort.active},${this.sort.direction}`,
                        f.title ?? '',
                        f.publisher ?? [],
                        f.format ?? [],
                        f.author ?? '',
                        f.illustrator ?? '',
                    )
                })
            )
            .subscribe(resp => {
                this.dataSource.data = resp.content;
                //this.dataSource.paginator = this.paginator;
                this.paginator.length = resp.page.totalElements;
            });
    }


    searchComics() {
        if (this.searchKeyword().trim().length > 2) {
            this.comicService.getComicsBySearchable(this.pageNumber(), this.searchKeyword()).subscribe(response => {
                const ownedIds = new Set(this.userComicService.getComicsObject().map(u => u.comic.id));
                const wishlistedIds = new Set(this.userComicService.getWishlistedComicsObject().map(u => u.comic.id));
                this.searchedComics.set(response.content.filter(c => !ownedIds.has(c.id) && !wishlistedIds.has(c.id)));
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
        } else {
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

    createComic() {
        this.dialog.open(addComicComponent, {
            disableClose: true,
            autoFocus: false,
             data: {
                step: this.ComicCreationStepEnum.SCAN_OR_MANUAL_CREATE_DIALOG,
            },
        });
    }

    resetFilters() {
        this.filterForm.reset({
            title: '',
            author: '',
            illustrator: '',
            publisher: [],
            format: [],
        });
    }

}