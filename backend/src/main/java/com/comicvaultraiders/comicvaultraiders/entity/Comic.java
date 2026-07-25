package com.comicvaultraiders.comicvaultraiders.entity;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comic")
@NamedQueries({
        @NamedQuery(
                name = "Comic.findBySearchFilter",
                query = "SELECT b FROM Comic b WHERE lower(b.title) like lower(:title) or lower(b.author) like lower(:author)"
        ),
        @NamedQuery(
                name = "Comic.findAllWithoutUser",
                query = "SELECT c FROM Comic c LEFT JOIN c.userXComics uc ON uc.user.id = :userId WHERE uc IS NULL"),
        @NamedQuery(
                name = "Comic.findCorruptDataComics",
                query = "SELECT c FROM Comic c where crd between :fromDate AND :toDate AND (c.checkedByRepairJob = false OR c.checkedByRepairJob IS NULL) AND ((c.coverImgUrl is null or trim(c.coverImgUrl) = '' ) or c.releaseDate IS null )"),
})
public class Comic {

    public Comic(ComicDto comicDto, Boolean isCheckedByRepairJob) {
        this.setTitle(comicDto.getTitle());
        this.setAuthor(comicDto.getAuthor());
        this.setPublisher(comicDto.getPublisher());
        this.setCoverImgUrl(comicDto.getCoverImgUrl());
        this.setReleaseDate(comicDto.getReleaseDate());
        this.setFormat(comicDto.getFormat());
        this.setIssueNumber(comicDto.getIssueNumber());
        this.setCheckedByRepairJob(isCheckedByRepairJob);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "illustrator")
    private String illustrator;

    @Column(name = "issue_number")
    private Long issueNumber;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "format")
    private String format;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "cover_img_url")
    private String coverImgUrl;

    @Column(name = "crd")
    private ZonedDateTime crd;

    @Column(name = "repair_job_checked")
    private Boolean checkedByRepairJob;

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserXComics> userXComics = new ArrayList<>();

}
