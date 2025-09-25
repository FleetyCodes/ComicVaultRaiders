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
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "issue_number")
    private Long issueNumber;

    @Column(name = "release_date")
    private ZonedDateTime releaseDate;

    @Column(name = "cover_img_url")
    private String coverImgUrl;

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserXComics> userXComics = new ArrayList<>();

}
