package com.comicvaultraiders.comicvaultraiders;

import com.comicvaultraiders.comicvaultraiders.model.Comic;
import com.comicvaultraiders.comicvaultraiders.model.User;
import com.comicvaultraiders.comicvaultraiders.model.UserRole;
import com.comicvaultraiders.comicvaultraiders.model.UserXComics;
import com.comicvaultraiders.comicvaultraiders.repository.ComicRepository;
import com.comicvaultraiders.comicvaultraiders.repository.UserRepository;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.comicvaultraiders.comicvaultraiders.repository.UserXComicsRepo;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class userXComicsIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private UserXComicsRepo userXComicsRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;

    private User testUser;
    private Comic testComic;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpw");
        testUser = userRepository.save(testUser);

        testComic = new Comic();
        testComic.setTitle("Batman");
        testComic.setReleaseDate(LocalDate.now());
        comicRepository.save(testComic);

        UserXComics uc = new UserXComics();
        uc.setUser(testUser);
        uc.setComic(testComic);
        userXComicsRepo.save(uc);
    }

    @Test
    public void testFilteredComics() throws Exception {

        User loggedInMock = new User();
        UserRole appUserRole = new UserRole();
        appUserRole.setId(1L);
        appUserRole.setName("APP_USER");
        loggedInMock.setUserRole(appUserRole);
        loggedInMock.setUsername("testuser");
        loggedInMock.setId(1L);
        String jwt = jwtUtil.generateToken(loggedInMock);

        mockMvc.perform(get("/v1/user/filteredComics")
                        .header("Authorization", "Bearer "+ jwt)
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchBy", "batman"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].comic.title").value("Batman"));
    }
}
