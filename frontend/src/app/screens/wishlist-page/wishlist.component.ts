import { CommonModule } from "@angular/common";
import { Component, OnInit, signal, ViewChild } from "@angular/core";
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
import { merge, debounceTime, startWith, switchMap } from "rxjs";
import { MatOption } from "@angular/material/select";
import { ComicFormatsEnum } from "../../models/comic.formats.enum";
import { ComicPublishersEnum } from "../../models/comic.publishers.enum";
import { MatTableModule } from '@angular/material/table';
import {  MatPaginatorModule } from "@angular/material/paginator";
import {  MatSortModule } from '@angular/material/sort';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

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
        MatProgressSpinnerModule
    ],
})


export class WishlistedComicsPageComponent implements OnInit {

    constructor(protected userComicService: UserComicsService) { }

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    publishers = Object.values(ComicPublishersEnum);
    comicFormats = Object.values(ComicFormatsEnum);

    protected hasComics = signal<boolean>(false);
    gridDataSource = new MatTableDataSource<UserComic>([]);
    displayedColumns = ['comic.title', 'comic.author', 'comic.illustrator', 'comic.publisher', 'comic.format', 'comic.releaseDate', 'comic.issueNumber', 'artRate', 'storyRate', 'panelRate'];

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
}