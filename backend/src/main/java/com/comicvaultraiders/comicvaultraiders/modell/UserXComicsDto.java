package com.comicvaultraiders.comicvaultraiders.modell;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserXComicsDto {

    private Long id;
    private ComicDto comic;
    private String positiveDescription;
    private String negativeDescription;
    private Long artRate;
    private Long storyRate;
    private Long panelRate;
    private Boolean wishlisted;

    public UserXComicsDto(UserXComics UserXComics) {
        this.id = UserXComics.getId();
        this.comic = new ComicDto(UserXComics.getComic());
        this.positiveDescription = UserXComics.getPositiveDescription();
        this.negativeDescription = UserXComics.getNegativeDescription();
        this.artRate = UserXComics.getArtRate();
        this.storyRate = UserXComics.getStoryRate();
        this.panelRate = UserXComics.getPanelRate();
        this.wishlisted = UserXComics.getWishlisted();
    }
}



