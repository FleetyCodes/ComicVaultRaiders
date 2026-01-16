package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.model.RefreshToken;
import com.comicvaultraiders.comicvaultraiders.model.User;
import com.comicvaultraiders.comicvaultraiders.repository.RefreshTokenRepository;
import com.comicvaultraiders.comicvaultraiders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository repo, UserRepository userRepo) {
        this.refreshTokenRepository = repo;
        this.userRepository = userRepo;
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken token = new RefreshToken();
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            token.setUser(user.get());
            token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            token.setToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(token);
        }
        throw new UsernameNotFoundException("User not found");
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public Optional<RefreshToken> findByToken(String requestToken) {
        return refreshTokenRepository.findByToken(requestToken);
    }

    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void deleteAllByUserId(Long userID){
        refreshTokenRepository.deleteAllByUserId(userID);
    }
}