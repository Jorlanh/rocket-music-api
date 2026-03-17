// src/main/java/com/rocketmusic/api/social/service/ReviewService.java
package com.rocketmusic.api.social.service;

import com.rocketmusic.api.social.dto.ReviewDto;
import com.rocketmusic.api.social.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    
    @Transactional
    public ReviewDto createReview(UUID userId, String albumId, int stars, String text) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Avaliação deve ser entre 1 e 5.");
        }
        // TODO: Mapear para entidade, salvar e retornar DTO
        return new ReviewDto(userId, albumId, stars, text);
    }
}