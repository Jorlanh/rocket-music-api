package com.rocketmusic.api.worker.dto;

import java.time.LocalDateTime;

public record ScrobbleRecord(
    String trackId,
    int durationMs,
    LocalDateTime playedAt
) {}