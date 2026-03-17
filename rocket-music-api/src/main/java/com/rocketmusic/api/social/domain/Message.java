package com.rocketmusic.api.social.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity 
@Table(name = "direct_messages") @Getter @Setter
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime sentAt = LocalDateTime.now();
}