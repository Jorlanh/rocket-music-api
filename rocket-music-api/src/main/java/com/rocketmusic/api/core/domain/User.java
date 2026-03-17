package com.rocketmusic.api.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String spotifyId;
    
    private String accessToken;
    private String refreshToken;
    
    private String userStatus;
    private String instagramHandle;
}