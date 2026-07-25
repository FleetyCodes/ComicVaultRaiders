package com.comicvaultraiders.comicvaultraiders.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ai_rate_limit")
public class AiRateLimit {

    public AiRateLimit(Long userId, Long traffic) {
        this.setUserId(userId);
        this.setTraffic(traffic);
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "traffic")
    private Long traffic;



}
