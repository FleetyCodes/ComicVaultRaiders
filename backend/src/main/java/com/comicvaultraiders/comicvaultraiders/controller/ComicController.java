package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.ComicDto;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("v1/comic")
public class ComicController {

    private final ComicService comicService;
    private final JwtUtil jwtUtils;

    public ComicController(ComicService comicService, JwtUtil jwtUtils) {
        this.comicService = comicService;
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
        String jwt = "";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        if (!jwt.isBlank() && jwtUtils.validateJwtToken(jwt)) {
            return ResponseEntity.ok(comicService.getAllComicsWithoutUsers(jwt));
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JWT");
            //return ResponseEntity.badRequest().body("Invalid JWT");
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

}
