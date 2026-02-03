package com.comicvaultraiders.comicvaultraiders.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comic_create_queue")
public class ComicBulkCreateQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "keyword")
    private String keyword;

    @Column(name ="startindex")
    private Long startIndex;

}
