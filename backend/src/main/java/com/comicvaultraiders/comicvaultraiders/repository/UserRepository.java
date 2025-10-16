package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(name = "User.findUserByUserId")
    User findUserById(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginDate = :loginDate WHERE u.username = :username")
    int setLastLoginDateByUsername(@Param("username") String username, @Param("loginDate") ZonedDateTime loginDate);

}
