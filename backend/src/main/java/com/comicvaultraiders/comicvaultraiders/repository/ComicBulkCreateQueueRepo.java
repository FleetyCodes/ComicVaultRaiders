package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.model.ComicBulkCreateQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComicBulkCreateQueueRepo extends JpaRepository<ComicBulkCreateQueue, Long> {

    boolean existsByKeyword(String keyword);

    @Modifying
    @Query("DELETE FROM ComicBulkCreateQueue c where c.id = :id ")
    void deleteAllById(@Param("id") Long id);

}
