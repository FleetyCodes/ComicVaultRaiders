package com.comicvaultraiders.comicvaultraiders.service;


import com.comicvaultraiders.comicvaultraiders.repository.ComicRepository;
import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComicService {

    private final ComicRepository comicRepository;


    public ComicService(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
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
}
