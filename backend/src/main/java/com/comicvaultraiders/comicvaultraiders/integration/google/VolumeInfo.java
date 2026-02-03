package com.comicvaultraiders.comicvaultraiders.integration.google;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo {
    @lombok.Getter(onMethod_ = {@JsonProperty("title")})
    @lombok.Setter(onMethod_ = {@JsonProperty("title")})
    private String title;
    @lombok.Getter(onMethod_ = {@JsonProperty("authors")})
    @lombok.Setter(onMethod_ = {@JsonProperty("authors")})
    private String[] authors;
    @lombok.Getter(onMethod_ = {@JsonProperty("publisher")})
    @lombok.Setter(onMethod_ = {@JsonProperty("publisher")})
    private String publisher;
    @lombok.Getter(onMethod_ = {@JsonProperty("publishedDate")})
    @lombok.Setter(onMethod_ = {@JsonProperty("publishedDate")})
    private String publishedDate;
    @lombok.Getter(onMethod_ = {@JsonProperty("imageLinks")})
    @lombok.Setter(onMethod_ = {@JsonProperty("imageLinks")})
    private ImageLinks imageLinks;
    @lombok.Getter(onMethod_ = {@JsonProperty("subtitle")})
    @lombok.Setter(onMethod_ = {@JsonProperty("subtitle")})
    private String subtitle;
}
