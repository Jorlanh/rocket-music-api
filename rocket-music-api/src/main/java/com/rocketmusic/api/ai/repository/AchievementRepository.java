package com.rocketmusic.api.ai.repository;

import com.rocketmusic.api.ai.domain.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<UserAchievement, UUID> {
}