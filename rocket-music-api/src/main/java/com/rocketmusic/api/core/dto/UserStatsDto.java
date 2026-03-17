package com.rocketmusic.api.core.dto;

import java.util.UUID;

// Utilizando Java Records para DTOs imutáveis (Java 14+)
public record UserStatsDto(
    UUID userId,
    long totalMinutes,
    long totalPlays
) {}