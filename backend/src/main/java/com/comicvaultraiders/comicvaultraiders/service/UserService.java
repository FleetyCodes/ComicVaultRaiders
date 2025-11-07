package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.exception.UserAlreadyExistsException;
import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.User;
import com.comicvaultraiders.comicvaultraiders.modell.UserXComics;
import com.comicvaultraiders.comicvaultraiders.modell.UserXComicsDto;
import com.comicvaultraiders.comicvaultraiders.repository.UserRepository;
import com.comicvaultraiders.comicvaultraiders.repository.UserXComicsRepo;
import com.comicvaultraiders.comicvaultraiders.util.EncryptionUtil;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    public UserService(UserRepository userRepository, UserXComicsRepo userXComicsRepo, EncryptionUtil encryptionUtil, JwtUtil jwtUtils) {
        this.userRepository = userRepository;
        this.userXComicsRepo = userXComicsRepo;
        this.encryptionUtil = encryptionUtil;
        this.jwtUtils = jwtUtils;
    }

    private final UserRepository userRepository;
    private final UserXComicsRepo userXComicsRepo;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtils;

    @Transactional
    public User createUser(User user) {
        try{
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            String encryptedUsername = encryptionUtil.encrypt(user.getUsername(), encryptionUtil.getSecretKeyString());
            user.setUsername(encryptedUsername);
            if(userRepository.findByUsername(encryptedUsername).isPresent()){
                throw new UserAlreadyExistsException("User already exists");
            }
            String encryptedEmail = encryptionUtil.encrypt(user.getEmail(), encryptionUtil.getSecretKeyString());
            user.setEmail(encryptedEmail);
            user.setConfirmed(false);
            user.setDeleted(false);
            user.setRegDate(ZonedDateTime.now(ZoneId.of("UTC")));
        }catch (GeneralSecurityException e) {
            throw new RuntimeException("Encryption failed", e);
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User login(User user) {
        try{
            String encData = encryptionUtil.encrypt(user.getUsername(), encryptionUtil.getSecretKeyString());
            Optional<User> checkUser = userRepository.findByUsername(encData);

            if(checkUser.isEmpty()){
                return null;
            }
            if(passwordEncoder.matches(user.getPassword(), checkUser.get().getPassword())){
                int updatedRows = userRepository.setLastLoginDateByUsername(encData, ZonedDateTime.now(ZoneId.of("UTC")));
                if(updatedRows>0){
                    return checkUser.get();
                }else{
                    throw new UsernameNotFoundException("User Not Found with username: " + user.getUsername());
                }
            }else{
                return null;
            }

        }catch(GeneralSecurityException e){
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username).get();
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).get();
        user.setDeleted(true);
        user.setDeleteDate(ZonedDateTime.now(ZoneId.of("UTC")));
        userRepository.save(user);
    }

    public List<UserXComicsDto> getUserComics(Long userId) {
        try{
            User tmpUser = userRepository.findUserById(userId);
            return tmpUser.getUserXComics().stream()
                    .map(uxc -> new UserXComicsDto(uxc))
                    .toList();
        }catch(Exception e){
            return null;
        }
    }

    public Long getUserId(String token) {
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            return user.get().getId();
        }else{
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
    }

    @Transactional
    public void removeUserComic(Long userXComicId) {
        userXComicsRepo.deleteById(userXComicId);
    }

    @Transactional
    public UserXComicsDto addUserComics(Long userId, Comic comic) {
        UserXComics userXComics = new UserXComics();
        Optional<User> user = getUserById(userId);
        if(user.isPresent()){
            userXComics.setUser(user.get());
        }else{
            throw new EntityNotFoundException();
        }
        userXComics.setComic(comic);
        userXComics.setArtRate(0L);
        userXComics.setPanelRate(0L);
        userXComics.setStoryRate(0L);
        userXComics.setWishlisted(false);
        UserXComics saveUserComic = userXComicsRepo.save(userXComics);
        return new UserXComicsDto(saveUserComic);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
