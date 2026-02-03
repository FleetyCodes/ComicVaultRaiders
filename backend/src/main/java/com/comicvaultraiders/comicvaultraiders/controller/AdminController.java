package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.model.ComicBulkCreateQueue;
import com.comicvaultraiders.comicvaultraiders.service.ComicBulkCreateQueueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("v1/admin")
public class AdminController {

    private final ComicBulkCreateQueueService comicBulkCreateQueueService;


    public AdminController(ComicBulkCreateQueueService comicBulkCreateQueueService) {
        this.comicBulkCreateQueueService = comicBulkCreateQueueService;
    }

    @PostMapping("/scrapeComics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> scrapeComics(@Valid @RequestBody String scrapeKeyword) {
        boolean isKeywordInQueue = comicBulkCreateQueueService.isKeyWordInQueue(scrapeKeyword);
        if(isKeywordInQueue){
            return ResponseEntity.ok(0);
        }else{
            ComicBulkCreateQueue insertRow = new ComicBulkCreateQueue();
            insertRow.setKeyword(scrapeKeyword);
            insertRow.setStartIndex(1L);
            comicBulkCreateQueueService.putKeyWordInQueue(insertRow);
            return ResponseEntity.ok(1);
        }
    }
}
