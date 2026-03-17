package com.rocketmusic.api.ai.controller;

import com.rocketmusic.api.ai.integration.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final GeminiClient geminiClient;

    @PostMapping("/recommendations")
    public String getRecommendations(@RequestBody String userMessage) {
        String prompt = "Você é o assistente de elite Rocket AI. " +
                       "Baseado na mensagem do usuário: '" + userMessage + "', " +
                       "recomende 3 álbuns musicais obscuros e interessantes.";
        
        return geminiClient.generateContent(prompt);
    }
}