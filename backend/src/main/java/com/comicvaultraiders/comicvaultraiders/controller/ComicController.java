package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.ComicDto;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import com.comicvaultraiders.comicvaultraiders.service.GoogleAPIService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/comic")
public class ComicController {

    private final ComicService comicService;
    private final GoogleAPIService googleAPIService;
    private final JwtUtil jwtUtils;

    public ComicController(ComicService comicService, GoogleAPIService googleAPIService, JwtUtil jwtUtils) {
        this.comicService = comicService;
        this.googleAPIService = googleAPIService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComicDto> getComicById(@PathVariable Long id) {
        return comicService.getComicById(id)
                .map(item -> ResponseEntity.ok(new ComicDto (item)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ComicDto> getAllComics(){
        return comicService.getAllComics()
                .stream()
                    .map(ComicDto::new)
                    .toList();
    }

    @GetMapping("/exceptUser/")
    public ResponseEntity<?>  getAllComicsExcludeUsers(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(comicService.getAllComicsWithoutUsers(jwtUtils.getJwtFromHeader(authHeader)));
    }

    @PostMapping
    public ResponseEntity<ComicDto> createComic(@Valid  @RequestBody Comic comic) {
        Optional<Comic> newComic = comicService.createComic(comic);
        return newComic.map(item -> ResponseEntity.ok(new ComicDto (item)))
                .orElse(ResponseEntity.notFound().build());
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

    @GetMapping(params = "searchBy")
    public ResponseEntity<Page<ComicDto>> getComics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "6") int size,
            @RequestParam() String searchBy) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("title").ascending());

        Page<ComicDto> comics = comicService.getFilteredComics(pageable, searchBy);
        return ResponseEntity.ok(comics);
    }

    @GetMapping("/info/")
    public ResponseEntity<?>  getComicInfo(@RequestHeader("Authorization") String authHeader, @RequestParam() String barcode){
        return ResponseEntity.ok(googleAPIService.getComicInfo(barcode));
    }

}
