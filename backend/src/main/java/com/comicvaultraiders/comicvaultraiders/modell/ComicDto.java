package com.comicvaultraiders.comicvaultraiders.modell;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
public class ComicDto {
    private Long id;
    private String title;
    private String author;
    private String illustrator;
    private Long issueNumber;
    private ZonedDateTime releaseDate;
    private String coverImgUrl;
    private String publisher;
    private String format;

    public ComicDto(Comic comic) {
        this.id = comic.getId();
        this.title = comic.getTitle();
        this.author = comic.getAuthor();
        this.illustrator = comic.getIllustrator();
        this.issueNumber = comic.getIssueNumber();
        this.releaseDate = comic.getReleaseDate();
        this.coverImgUrl = comic.getCoverImgUrl();
        this.publisher = comic.getPublisher();
        this.format = comic.getFormat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, releaseDate, format);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComicDto)) return false;
        ComicDto param = (ComicDto) obj;
        return Objects.equals(id, param.id) && Objects.equals(title, param.title) && Objects.equals(releaseDate, param.releaseDate) && Objects.equals(format, param.format);
    }


}
