package com.rocketmusic.api.ai.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MilestoneEvent {
    private final UUID userId;
    private final String username;
    private final long minutes;
    private final String milestoneType;
}