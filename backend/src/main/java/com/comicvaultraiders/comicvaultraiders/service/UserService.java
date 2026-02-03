package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.dto.UserXComicsDto;
import com.comicvaultraiders.comicvaultraiders.dto.filter.UserComicFilter;
import com.comicvaultraiders.comicvaultraiders.exception.UserAlreadyExistsException;
import com.comicvaultraiders.comicvaultraiders.model.*;
import com.comicvaultraiders.comicvaultraiders.repository.UserRepository;
import com.comicvaultraiders.comicvaultraiders.repository.UserXComicsRepo;
import com.comicvaultraiders.comicvaultraiders.specification.UserComicSpecs;
import com.comicvaultraiders.comicvaultraiders.util.EncryptionUtil;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
            if(user.getEmail()!=null){
                String encryptedEmail = encryptionUtil.encrypt(user.getEmail(), encryptionUtil.getSecretKeyString());
                user.setEmail(encryptedEmail);
            }
            user.setConfirmed(false);
            user.setDeleted(false);
            user.setRegDate(ZonedDateTime.now(ZoneId.of("UTC")));
            UserRole appUserRole = new UserRole();
            appUserRole.setId(1L);
            appUserRole.setName("APP_USER");
            user.setUserRole(appUserRole);
        } catch (GeneralSecurityException e) {
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
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            String role = user.get().getUserRole().getName();
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    authorities);
        }else{
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setDeleted(true);
            user.get().setDeleteDate(ZonedDateTime.now(ZoneId.of("UTC")));
            userRepository.save(user.get());
        }else {
            throw new UsernameNotFoundException("User not found");
        }
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
         return jwtUtils.getUserIdFromToken(token);
    }

    @Transactional
    public void removeUserComic(Long userXComicId) {
        userXComicsRepo.deleteById(userXComicId);
    }

    @Transactional
    public UserXComicsDto addUserComics(Long userId, Comic comic, UserXComics newComic) {
        UserXComics userXComics = new UserXComics();
        Optional<User> user = getUserById(userId);
        if(user.isPresent()){
            userXComics.setUser(user.get());
        }else{
            throw new EntityNotFoundException();
        }
        userXComics.setComic(comic);
        userXComics.setArtRate(newComic.getArtRate()==null ? 0L : newComic.getArtRate());
        userXComics.setPanelRate(newComic.getPanelRate()==null ? 0L : newComic.getPanelRate());
        userXComics.setStoryRate(newComic.getStoryRate()==null ? 0L : newComic.getStoryRate());
        userXComics.setWishlisted(newComic.getWishlisted()!=null && newComic.getWishlisted());
        userXComics.setPositiveDescription(newComic.getPositiveDescription()==null ? "" : newComic.getPositiveDescription());
        userXComics.setNegativeDescription(newComic.getNegativeDescription()==null ? "" : newComic.getNegativeDescription());
        UserXComics saveUserComic = userXComicsRepo.save(userXComics);
        return new UserXComicsDto(saveUserComic);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserXComicsDto> getUserFilteredComics(UserComicFilter filter, Pageable pageable) {
        Specification<UserXComics> spec = UserComicSpecs.withFilters(filter);
        return  userXComicsRepo.findAll(spec, pageable).map(UserXComicsDto::new);
    }
}
