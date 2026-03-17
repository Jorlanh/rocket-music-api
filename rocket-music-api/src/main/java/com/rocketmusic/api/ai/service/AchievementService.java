// src/main/java/com/rocketmusic/api/ai/service/AchievementService.java
package com.rocketmusic.api.ai.service;

import com.rocketmusic.api.ai.integration.GeminiClient;
import com.rocketmusic.api.ai.repository.AchievementRepository;
import com.rocketmusic.api.ai.domain.UserAchievement;
import com.rocketmusic.api.ai.event.MilestoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final GeminiClient geminiClient; // RestClient configurado para a API do Google
    private final AchievementRepository achievementRepository;

    @Async
    @EventListener
    public void onMilestoneReached(MilestoneEvent event) {
        String prompt = String.format(
            "Gere uma URL ou descrição curta de uma imagem estilo badge retro-futurista para um usuário que ouviu %d minutos de música espacial. O nome do usuário é %s.",
            event.getMinutes(), event.getUsername()
        );

        String badgeUrl = geminiClient.generateContent(prompt);
        
        achievementRepository.save(new UserAchievement(event.getUserId(), event.getMilestoneType(), badgeUrl));
    }
}