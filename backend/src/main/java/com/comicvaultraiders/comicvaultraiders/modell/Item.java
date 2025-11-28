package com.comicvaultraiders.comicvaultraiders.modell;

import com.fasterxml.jackson.annotation.*;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @lombok.Getter(onMethod_ = {@JsonProperty("volumeInfo")})
    @lombok.Setter(onMethod_ = {@JsonProperty("volumeInfo")})
    private VolumeInfo volumeInfo;
}
