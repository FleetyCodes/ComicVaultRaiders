package com.libraryofsolitude.libraryofsolitude.service;


import com.libraryofsolitude.libraryofsolitude.entity.Comic;
import com.libraryofsolitude.libraryofsolitude.repository.ComicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComicService {

    private final ComicRepository comicRepository;


    public ComicService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    public Comic saveComic(Comic comic) {
        return comicRepository.save(comic);
    }

    public Optional<Comic> getComicById(Long id) {
        return comicRepository.findById(id);
    }

    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }
}
