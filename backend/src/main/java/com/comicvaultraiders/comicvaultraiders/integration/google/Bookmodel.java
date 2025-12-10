package com.comicvaultraiders.comicvaultraiders.integration.google;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bookmodel {
    @lombok.Getter(onMethod_ = {@JsonProperty("kind")})
    @lombok.Setter(onMethod_ = {@JsonProperty("kind")})
    private String kind;
    @lombok.Getter(onMethod_ = {@JsonProperty("totalItems")})
    @lombok.Setter(onMethod_ = {@JsonProperty("totalItems")})
    private Long totalItems;
    @lombok.Getter(onMethod_ = {@JsonProperty("items")})
    @lombok.Setter(onMethod_ = {@JsonProperty("items")})
    private Item[] items;
}
