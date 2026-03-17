// src/main/java/com/rocketmusic/api/core/controller/StatsController.java
package com.rocketmusic.api.core.controller;

import com.rocketmusic.api.core.repository.UserStatsRepository;
import com.rocketmusic.api.core.dto.UserStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserStatsRepository statsRepository;

    @GetMapping("/me")
    public ResponseEntity<UserStatsDto> getMyRetrospective(@AuthenticationPrincipal OAuth2User principal) {
        String spotifyId = principal.getName();
        // Leitura ultrarrápida da Materialized View
        UserStatsDto stats = statsRepository.findStatsBySpotifyId(spotifyId);
        return ResponseEntity.ok(stats);
    }
}