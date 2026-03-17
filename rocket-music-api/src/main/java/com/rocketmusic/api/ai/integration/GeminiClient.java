package com.rocketmusic.api.ai.integration;

import org.springframework.stereotype.Component;

@Component
public class GeminiClient {
    
    // TODO: Implementar chamada real para api.google.com/gemini
    public String generateContent(String prompt) {
        return "https://rocketmusic.com/assets/badges/placeholder.png";
    }
}