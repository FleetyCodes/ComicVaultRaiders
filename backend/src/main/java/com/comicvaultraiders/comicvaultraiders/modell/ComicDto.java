package com.comicvaultraiders.comicvaultraiders.modell;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ComicDto {
    private Long id;
    private String title;
    private String author;
    private Long issueNumber;
    private ZonedDateTime releaseDate;
    private String coverImgUrl;

    public ComicDto(Comic comic) {
        this.id = comic.getId();
        this.title = comic.getTitle();
        this.author = comic.getAuthor();
        this.issueNumber = comic.getIssueNumber();
        this.releaseDate = comic.getReleaseDate();
        this.coverImgUrl = comic.getCoverImgUrl();
    }
}
