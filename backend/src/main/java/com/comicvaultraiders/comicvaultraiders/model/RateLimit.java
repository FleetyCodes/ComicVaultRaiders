package com.comicvaultraiders.comicvaultraiders.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rate_limit")
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "api_name")
    private String apiName;

    @Column(name ="daily_limit")
    private Long dailyLimit;

    @Column(name ="traffic")
    private Long traffic;
}
