package com.comicvaultraiders.comicvaultraiders.modell;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_user")
@NamedQueries({
        @NamedQuery(
                name = "User.findUserByUserId",
                query = "SELECT b FROM User b WHERE b.id = :userId"
        )
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "registration_date")
    private ZonedDateTime regDate;

    @Column(name = "last_login_date")
    private ZonedDateTime lastLoginDate;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "delete_date")
    private ZonedDateTime deleteDate;

    @Column(name = "confirmed")
    private boolean isConfirmed;

    //@NotBlank(message = "E-mail address is required")
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserXComics> userXComics = new ArrayList<>();

}

