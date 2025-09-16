package com.libraryofsolitude.libraryofsolitude.repository;

import com.libraryofsolitude.libraryofsolitude.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {

}
