package com.comicvaultraiders.comicvaultraiders.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserComicFilter {
    private Long userId;
    private String title;
    private String author;
    private String illustrator;
    private List<String> publisher;
    private List<String> format;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean wishlisted;
}
