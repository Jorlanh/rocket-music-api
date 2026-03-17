package com.rocketmusic.api.social.dto;

import java.util.UUID;

public record ReviewDto(
    UUID userId,
    String albumId,
    int stars,
    String text
) {}