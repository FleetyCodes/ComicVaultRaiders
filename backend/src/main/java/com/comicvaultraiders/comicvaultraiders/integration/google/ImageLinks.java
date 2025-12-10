package com.comicvaultraiders.comicvaultraiders.integration.google;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageLinks {
    @lombok.Getter(onMethod_ = {@JsonProperty("thumbnail")})
    @lombok.Setter(onMethod_ = {@JsonProperty("thumbnail")})
    private String thumbnail;
}
