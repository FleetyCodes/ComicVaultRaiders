package com.comicvaultraiders.comicvaultraiders.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserComicFilter {
    private Long userId;
    private String publisher;
    private String format;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean wishlisted;
}
