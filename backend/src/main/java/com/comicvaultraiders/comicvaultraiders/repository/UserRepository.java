package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);


    @Query(name = "User.findComicsByUser")
    List<Comic> findComicsByUser(@Param("userId") Long userId);
}
