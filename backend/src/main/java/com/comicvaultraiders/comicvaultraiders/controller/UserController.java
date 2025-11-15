package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.modell.*;
import com.comicvaultraiders.comicvaultraiders.service.RefreshTokenService;
import com.comicvaultraiders.comicvaultraiders.service.UserService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("v1/user")
public class UserController {

    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtUtil jwtUtils) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
    }

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtils;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user, HttpServletResponse response) {
        User loggedIn = userService.login(user);
        if (loggedIn != null) {
            refreshTokenService.deleteAllByUserId(loggedIn.getId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loggedIn.getId());
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/v1/user")
                    .maxAge(Duration.ofDays(15))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(Map.of("token", jwtUtils.generateToken(loggedIn.getUsername(), loggedIn.getId())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reg")
    public ResponseEntity<Void> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> {
                    if (refreshTokenService.isTokenExpired(token)) {
                        refreshTokenService.delete(token);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please login again.");
                    }
                    String newJwt = jwtUtils.generateToken(token.getUser().getUsername(), token.getUser().getId());
                    if(token.getExpiryDate().isBefore(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(1).toInstant())){
                        refreshTokenService.delete(token);
                        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser().getId());
                        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("Strict")
                                .path("/v1/user")
                                .maxAge(Duration.ofDays(15))
                                .build();
                        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                    }
                    return ResponseEntity.ok(Map.of("token", newJwt));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please login again."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }
        if(refreshTokenService.findByToken(refreshToken).isEmpty()){
            return ResponseEntity.badRequest().body("Invalid refresh token.");
        }
        RefreshToken rToken = refreshTokenService.findByToken(refreshToken).get();
        refreshTokenService.delete(rToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/v1/user")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/comics")
    public ResponseEntity<?> getUserComics(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getUserComics(jwtUtils.getUserIdFromToken(jwtUtils.getJwtFromheader(authHeader))));
    }

    @DeleteMapping("/comics/{comicId}")
    public ResponseEntity<?> deleteUserComic(@PathVariable Long comicId){
        userService.removeUserComic(comicId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comic/{comicId}")
    public ResponseEntity<?> addComic(@RequestHeader("Authorization") String authHeader, @RequestBody UserXComics userComic){
        return ResponseEntity.ok(userService.addUserComics(jwtUtils.getUserIdFromToken(jwtUtils.getJwtFromheader(authHeader)),userComic.getComic(), userComic));
    }

}
