package com.rocketmusic.api.worker.integration;

import com.rocketmusic.api.worker.dto.ScrobbleRecord;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Component
public class SpotifyApiClient {
    
    // TODO: Implementar chamadas reais usando RestTemplate ou WebClient
    public String refreshUserToken(String refreshToken) {
        return "mocked_access_token"; 
    }

    public List<ScrobbleRecord> getRecentlyPlayed(String validToken) {
        return new ArrayList<>(); 
    }
}