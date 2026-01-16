package com.comicvaultraiders.comicvaultraiders.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_comic")
public class UserXComics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    @Column(name = "positive_description", length = 1000)
    private String positiveDescription;

    @Column(name = "negative_description", length = 1000)
    private String negativeDescription;

    @Column(name = "art_rate")
    private Long artRate;

    @Column(name = "story_rate")
    private Long storyRate;

    @Column(name = "panel_rate")
    private Long panelRate;

    @Column(name = "wishlisted")
    private Boolean wishlisted;


}
