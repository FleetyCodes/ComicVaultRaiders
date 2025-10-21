package com.comicvaultraiders.comicvaultraiders.service;


import com.comicvaultraiders.comicvaultraiders.modell.ComicDto;
import com.comicvaultraiders.comicvaultraiders.modell.UserXComicsDto;
import com.comicvaultraiders.comicvaultraiders.repository.ComicRepository;
import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ComicService {

    private final ComicRepository comicRepository;
    private final UserService userService;


    public ComicService(ComicRepository comicRepository, UserService userService) {
        this.comicRepository = comicRepository;
        this.userService = userService;
    }

    @Transactional
    public Comic createComic(Comic comic) {
        return comicRepository.save(comic);
    }

    @Transactional
    public Comic updateComic(Long id, Comic comicDetails) {
        return comicRepository.findById(id)
                .map(comic -> {
                    comic.setTitle(comicDetails.getTitle());
                    comic.setAuthor(comicDetails.getAuthor());
                    comic.setCoverImgUrl(comicDetails.getCoverImgUrl());
                    comic.setIssueNumber(comicDetails.getIssueNumber());
                    comic.setReleaseDate(comicDetails.getReleaseDate());
                    return comicRepository.save(comic);
                })
                .orElseThrow(() -> new EntityNotFoundException("Comic not found with id " + id));
    }
    public Optional<Comic> getComicById(Long id) {
        return comicRepository.findById(id);
    }

    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }

    public List<ComicDto> getAllComicsWithoutUsers(String token){
        List<ComicDto> allComics = getAllComics().stream().map(ComicDto::new).toList();
        Long userId = userService.getUserId(token);
        Set<Long> userComicIds = userService.getUserComics(userId)
                .stream()
                .map(usercom -> usercom.getComic().getId())
                .collect(Collectors.toSet());
        return allComics.stream()
                .filter(c -> !userComicIds.contains(c.getId()))
                .toList();
    }
}
