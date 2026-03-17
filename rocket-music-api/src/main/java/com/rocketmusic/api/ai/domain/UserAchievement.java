package com.rocketmusic.api.ai.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_achievements")
public class UserAchievement {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private String milestoneType;
    private String badgeUrl;

    protected UserAchievement() {}

    public UserAchievement(UUID userId, String milestoneType, String badgeUrl) {
        this.userId = userId;
        this.milestoneType = milestoneType;
        this.badgeUrl = badgeUrl;
    }
}