package com.comicvaultraiders.comicvaultraiders.controller;

import com.comicvaultraiders.comicvaultraiders.modell.User;
import com.comicvaultraiders.comicvaultraiders.modell.UserXComicsDto;
import com.comicvaultraiders.comicvaultraiders.service.RefreshTokenService;
import com.comicvaultraiders.comicvaultraiders.service.UserService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        User loggedIn = userService.login(user);
        if (loggedIn != null) {
            return ResponseEntity.ok(Map.of("token", jwtUtils.generateToken(loggedIn.getUsername())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        return refreshTokenService.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenService.isTokenExpired(token)) {
                        refreshTokenService.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
                    }
                    String newJwt = jwtUtils.generateToken(token.getUser().getUsername());
                    return ResponseEntity.ok(Map.of("token", newJwt));
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if (requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }

        return refreshTokenService.findByToken(requestToken)
                .map(token -> {
                    refreshTokenService.delete(token);
                    return ResponseEntity.ok("Logged out successfully.");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }

    @GetMapping("/user/comics")
    public ResponseEntity<?> getUserComics(@RequestHeader("Authorization") String authHeader) {
        String jwt = "";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        if (!jwt.isBlank() && jwtUtils.validateJwtToken(jwt)) {
            Long userId = userService.getUserId(jwt);
            return ResponseEntity.ok(userService.getUserComics(userId));
        }else{
            return ResponseEntity.badRequest().body("Invalid JWT");
        }
    }

}
