package com.rocketmusic.api.social.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "reviews") @Getter @Setter
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private String albumId;
    private Double rating; // 1.0 a 5.0
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}