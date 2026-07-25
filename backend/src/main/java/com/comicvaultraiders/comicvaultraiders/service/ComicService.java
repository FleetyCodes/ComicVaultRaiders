package com.comicvaultraiders.comicvaultraiders.service;


import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.repository.ComicRepository;
import com.comicvaultraiders.comicvaultraiders.entity.Comic;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ComicService {
    private final ComicRepository comicRepository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public ComicService(ComicRepository comicRepository, UserService userService) {
        this.comicRepository = comicRepository;
        this.userService = userService;
    }

    @Transactional
    public Optional<Comic> createComic(Comic comic, boolean checkedByJob) {
        comic.setCrd(ZonedDateTime.now());
        comic.setCheckedByRepairJob(checkedByJob);
        return Optional.of(comicRepository.save(comic));
    }

    public Integer createBulkComics(List<ComicDto> scrapedComics) {
        int numofCreatedComics = 0;
        for(int i=0; i< scrapedComics.size(); i++){
            Comic saveComic = new Comic(scrapedComics.get(i), true);
            try{
                Optional<Comic> newComic = createComic(saveComic, true);
                if(newComic.isPresent()){
                    numofCreatedComics++;
                }
            }catch(DataIntegrityViolationException e){
                //duplicated exists, skip create
                logger.info("comic already exists: " + scrapedComics.get(i).getAuthor() + " - " + scrapedComics.get(i).getTitle());
            }
        }
        return numofCreatedComics;
    }

    @Transactional
    public Comic updateComic(Comic comicDetails) {
        return comicRepository.findById(comicDetails.getId())
                .map(comic -> {
                    comic.setTitle(comicDetails.getTitle());
                    comic.setAuthor(comicDetails.getAuthor());
                    comic.setCoverImgUrl(comicDetails.getCoverImgUrl());
                    comic.setIssueNumber(comicDetails.getIssueNumber());
                    comic.setReleaseDate(comicDetails.getReleaseDate());
                    comic.setCheckedByRepairJob(comicDetails.getCheckedByRepairJob());
                    return comicRepository.save(comic);
                })
                .orElseThrow(() -> new EntityNotFoundException("Comic not found with id " + comicDetails.getId()));
    }

    public Optional<Comic> getComicById(Long id) {
        return comicRepository.findById(id);
    }


    public List<Comic> getAllComics() {
        return comicRepository.findAll();
    }

    public List<ComicDto> getAllComicsWithoutUsers(String token){
        Long userId = userService.getUserId(token);
        return comicRepository.getAllComicsWithoutUser(userId).stream().map(ComicDto::new).toList();
    }

    public List<ComicDto> getAllComicsWithoutUser(Long userID){
        return comicRepository.getAllComicsWithoutUser(userID).stream().map(ComicDto::new).toList();
    }

    public Page<ComicDto> getFilteredComics(Pageable pageable, String searchBy) {
        searchBy = "%" + searchBy +"%";
        return comicRepository.getFilteredComics(searchBy, searchBy, pageable).map(ComicDto::new);
    }


    public List<Comic> getAllComicsWithCorruptedData(ZonedDateTime fromDate, ZonedDateTime toDate){
        return comicRepository.getAllComicsWithCorruptedData(fromDate, toDate);
    }
}
