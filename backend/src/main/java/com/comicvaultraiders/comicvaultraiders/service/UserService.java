package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.exception.UserAlreadyExistsException;
import com.comicvaultraiders.comicvaultraiders.modell.Comic;
import com.comicvaultraiders.comicvaultraiders.modell.User;
import com.comicvaultraiders.comicvaultraiders.repository.UserRepository;
import com.comicvaultraiders.comicvaultraiders.util.EncryptionUtil;
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

    public UserService(UserRepository userRepository, EncryptionUtil encryptionUtil) {
        this.userRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
    }

    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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

    public User login(User user)  {
        try{
            String encData = encryptionUtil.encrypt(user.getUsername(), encryptionUtil.getSecretKeyString());
            Optional<User> checkUser = userRepository.findByUsername(encData);

            if(checkUser.isEmpty()){
                return null;
            }

            if(passwordEncoder.matches(user.getPassword(), checkUser.get().getPassword())){
                return checkUser.get();
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

    public List<Comic> getUserComics(Long userId) {
        return userRepository.findComicsByUser(userId);
    }

}
