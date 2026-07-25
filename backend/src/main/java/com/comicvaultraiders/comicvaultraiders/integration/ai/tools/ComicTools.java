package com.comicvaultraiders.comicvaultraiders.integration.ai.tools;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComicTools {
    private final ComicService comicService;

    public ComicTools(ComicService comicService) {
        this.comicService = comicService;
    }

    @Tool(description = "Returns a comic's data by its ID")
    public ComicDto getComicById(Long id) {
        return comicService.getComicById(id).map(ComicDto::new).orElseThrow(() -> new RuntimeException("Comic not found with id: " + id));
    }

    @Tool(description = "Returns all comics from the database")
    public List<ComicDto> getAllComics() {
        return comicService.getAllComics().stream()
                .map(ComicDto::new)
                .toList();
    }

    @Tool(description = "Returns every comics which the current user does not have in his/her collection")
    public List<ComicDto> getAllComicsWithoutUser(Long userId){
        return comicService.getAllComicsWithoutUser(userId);
    }

    @Tool(description = "Returns comics by filter. searchBy parameter can be used for Author or Title based search.")
    public List<ComicDto> getFilteredComics(String searchBy) {
        Pageable pageable = Pageable.unpaged();
        return comicService.getFilteredComics(pageable, searchBy).stream().toList();
    }
}
