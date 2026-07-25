package com.comicvaultraiders.comicvaultraiders.integration.ai.tools;

import com.comicvaultraiders.comicvaultraiders.dto.UserXComicsDto;
import com.comicvaultraiders.comicvaultraiders.dto.filter.UserComicFilter;
import com.comicvaultraiders.comicvaultraiders.service.UserService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserTools {

    private final UserService userService;
    private final JwtUtil jwtUtils;
    public UserTools(UserService userService, JwtUtil jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Tool(description = "Returns the comics of the user, their current collection.")
    public List<UserXComicsDto> getUserComics(Long userId){
        return userService.getUserComics(userId);
    }

    @Tool(description = "Returns the user's comics. Multiple type of filters can be used.")
    public List<UserXComicsDto> getUserFilteredComics(Long UserId, String title, String author, String illustrator, List<String> publisher, List<String> format, Boolean wishlisted) {
        Pageable pageable = Pageable.unpaged();
        UserComicFilter filter = new UserComicFilter();
        filter.setUserId(UserId);
        filter.setTitle(title);
        filter.setAuthor(author);
        filter.setIllustrator(illustrator);
        filter.setPublisher(publisher);
        filter.setFormat(format);
        filter.setWishlisted(wishlisted);

        return userService.getUserFilteredComics(filter, pageable).stream().toList();

    }

}
