package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.ComicDto;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/comic")
public class ComicController {

    private final ComicService comicService;

    public ComicController(ComicService comicService) {
        this.comicService = comicService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComicDto> getComicById(@PathVariable Long id) {
        return comicService.getComicById(id)
                .map(item -> ResponseEntity.ok(new ComicDto (item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ComicDto> getAllComic(){
        return comicService.getAllComics()
                .stream()
                    .map(ComicDto::new)
                    .toList();
    }

    @GetMapping("/exceptUser/{token}")
    public List<ComicDto> getAllComic(@PathVariable String token){
        if(token != null && token !="" && !token.isEmpty()){
            return comicService.getAllComicsWithoutUsers(token);
        }else{
            return comicService.getAllComics()
                    .stream()
                    .map(ComicDto::new)
                    .toList();
        }
    }

    @PostMapping
    public Comic createComic(@RequestBody Comic comic) {
        return comicService.createComic(comic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comic> updateComic(
            @PathVariable Long id,
            @RequestBody Comic comicDetails) {
        try {
            Comic updated = comicService.updateComic(id, comicDetails);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
