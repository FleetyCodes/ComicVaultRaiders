package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {

}
