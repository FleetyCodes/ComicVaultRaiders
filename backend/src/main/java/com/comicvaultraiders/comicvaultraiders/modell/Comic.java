package com.comicvaultraiders.comicvaultraiders.modell;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "comic")
@NamedQueries({
        @NamedQuery(
                name = "Comic.findBySearchFilter",
                query = "SELECT b FROM Comic b WHERE lower(b.title) like lower(:title) or lower(b.author) like lower(:author)"
        ),
        @NamedQuery(
                name = "Comic.findAllWithoutUser",
                query = "SELECT c FROM Comic c LEFT JOIN c.userXComics uc ON uc.user.id = :userId WHERE uc IS NULL")
})
public class Comic {

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
    private ZonedDateTime releaseDate;

    @Column(name = "cover_img_url")
    private String coverImgUrl;

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserXComics> userXComics = new ArrayList<>();

}
