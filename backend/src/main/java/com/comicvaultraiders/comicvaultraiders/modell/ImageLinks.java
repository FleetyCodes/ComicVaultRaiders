package com.comicvaultraiders.comicvaultraiders.modell;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageLinks {
    @lombok.Getter(onMethod_ = {@JsonProperty("thumbnail")})
    @lombok.Setter(onMethod_ = {@JsonProperty("thumbnail")})
    private String thumbnail;
}
