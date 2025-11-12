package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {

    @Query(name = "Comic.findBySearchFilter")
    Page<Comic> getFilteredComics(@Param("title") String title, @Param("author") String author, Pageable pageable);

    @Query(name = "Comic.findAllWithoutUser")
    List<Comic> getAllComicsWithoutUser(@Param("userId") Long userId);

}
