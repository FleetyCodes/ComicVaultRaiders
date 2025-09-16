package com.libraryofsolitude.libraryofsolitude.controller;

import com.libraryofsolitude.libraryofsolitude.entity.Comic;
import com.libraryofsolitude.libraryofsolitude.repository.ComicRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comic")
public class ComicController {

    private final ComicRepository comicRepository;

    public ComicController(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comic> getComicById(@PathVariable Long id) {
        return comicRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Comic> getAllComic() {
        return comicRepository.findAll();
    }

    @PostMapping
    public Comic createComic(@RequestBody Comic comic) {
        return comicRepository.save(comic);
    }
}
