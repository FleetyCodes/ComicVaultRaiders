import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal, ViewChild } from "@angular/core";
import { RouterModule } from "@angular/router";
import { FormControl, FormGroup, FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { UserComic } from "../../models/user-comic";
import { UserComicComponent } from "../../components/comic-component/user-comic/user-comic-component";
import { UserComicsService } from "../../services/user.comic.service";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { merge, debounceTime, startWith, switchMap, Subject, finalize, interval, map, takeWhile, tap } from "rxjs";
import { MatOption } from "@angular/material/select";
import { ComicFormatsEnum } from "../../models/comic.formats.enum";
import { ComicPublishersEnum } from "../../models/comic.publishers.enum";
import { MatTableModule } from '@angular/material/table';
import {  MatPaginatorModule } from "@angular/material/paginator";
import {  MatSortModule } from '@angular/material/sort';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { Comic } from "../../models/comic";
import { ComicService } from "../../services/comic.service";
import { ComicComponent } from "../../components/comic-component/comic-component";
import { MatDialog } from "@angular/material/dialog";
import { addComicComponent } from "../../components/add-comic-dialog/add-comic.component";
import { ComicCreationStepEnum } from "../../models/comic.creation.step.enum";

@Component({
    selector: 'wishlist-page',
    templateUrl: './wishlist.component.html',
    styleUrls: ['./wishlist.component.scss'],
    standalone: true,
    imports: [
    FormsModule, MatFormFieldModule, MatInputModule,
    CommonModule,
    RouterModule, MatButtonModule,
    UserComicComponent,
    MatOption,
    MatPaginator,
    MatTableModule, MatPaginatorModule, MatSortModule, ReactiveFormsModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    ComicComponent
],
})


export class WishlistedComicsPageComponent implements OnInit {

    constructor(protected userComicService: UserComicsService, private comicService: ComicService) { }

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    publishers = Object.values(ComicPublishersEnum);
    comicFormats = Object.values(ComicFormatsEnum);

    protected hasComics = signal<boolean>(false);
    gridDataSource = new MatTableDataSource<UserComic>([]);
    displayedColumns = ['comic.title', 'comic.author', 'comic.illustrator', 'comic.publisher', 'comic.format', 'comic.releaseDate', 'comic.issueNumber', 'artRate', 'storyRate', 'panelRate'];
    
    protected searchKeyword = signal<string>("");
    protected searchedComics = signal<Comic[]>([]);
    protected searchErrMg = signal<string>("");
    protected showNotFoundError = signal<boolean>(false);
    private errorTrigger$ = new Subject<void>();
    protected opacity = signal<number>(0);
    protected totalElements = signal<number>(0);
    protected pageNumber = signal<number>(0);
    protected pageSize = signal<number>(0);
    private dialog = inject(MatDialog);
    ComicCreationStepEnum = ComicCreationStepEnum;

    filterForm = new FormGroup({
        title: new FormControl(''),
        author: new FormControl(''),
        illustrator: new FormControl(''),
        publisher: new FormControl<string[]>([]),
        format: new FormControl<string[]>([]),
    });


    ngOnInit() {
        this.userComicService.getUserComicsApi().subscribe({
            next: (response: UserComic[]) => {
                const filteredWishlistedComics = response.filter(uc => uc.wishlisted);
                this.userComicService.setWishlistedComicsObject(filteredWishlistedComics);
                if (this.userComicService.getWishlistedComicObjectCount() > 0) {
                    this.hasComics.set(true);
                }
            },
        });

        this.errorTrigger$
            .pipe(
                switchMap(() => {
                    this.showNotFoundError.set(true);
                    this.opacity.set(1);

                    return interval(50).pipe(
                        map(i => 1 - i * 0.02),
                        takeWhile(v => v >= 0),
                        tap(v => this.opacity.set(v)),
                        finalize(() => this.showNotFoundError.set(false))
                    );
                })
            )
            .subscribe();
    }

    ngAfterViewInit() {
        this.sort.active = 'comic.title';
        this.sort.direction = 'asc';

        this.sort.sortChange.subscribe(() => {
            this.paginator.pageIndex = 0;
        });

        this.filterForm.valueChanges.subscribe(() => {
            this.paginator.pageIndex = 0;
        });

        merge(
            this.paginator.page,
            this.sort.sortChange,
            this.filterForm.valueChanges.pipe(
                debounceTime(300),
                startWith(null),
            ),
        )
            .pipe(
                startWith(null),
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
                        true
                    )
                })
            )
            .subscribe(resp => {
                this.gridDataSource.data = resp.content;
                this.paginator.length = resp.page.totalElements;
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

    
    searchComics() {
        if (this.searchKeyword().trim().length > 2) {
            this.comicService.getComicsBySearchable(this.pageNumber(), this.searchKeyword()).subscribe(response => {
                const ownedIds = new Set(this.userComicService.getComicsObject().map(u => u.comic.id));
                const wishlistedIds = new Set(this.userComicService.getWishlistedComicsObject().map(u => u.comic.id));
                this.searchedComics.set(response.content.filter(c => !ownedIds.has(c.id) && !wishlistedIds.has(c.id)));
                if (this.searchedComics().length === 0) {
                    this.searchErrMg.set("Haven't found any comics with this parameter");
                    this.showNotFoundError.set(true);
                    this.errorTrigger$.next();
                }
                this.totalElements.set(response.totalElements);
                this.pageSize.set(response.size);
            });
        } else {
            this.searchedComics.set([]);
            this.searchErrMg.set("Please enter at least 3 characters");
            this.showNotFoundError.set(true);
            this.errorTrigger$.next();
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
}