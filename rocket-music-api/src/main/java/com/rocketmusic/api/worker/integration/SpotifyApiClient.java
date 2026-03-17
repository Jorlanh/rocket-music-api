package com.rocketmusic.api.worker.integration;

import com.rocketmusic.api.worker.dto.ScrobbleRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SpotifyApiClient {

    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String clientSecret;

    public String refreshUserToken(String refreshToken) {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);

        Map response = restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + encodedAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(Map.class);

        return (String) response.get("access_token");
    }

    public List<ScrobbleRecord> getRecentlyPlayed(String accessToken) {
        Map response = restClient.get()
                .uri("https://api.spotify.com/v1/me/player/recently-played?limit=50")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        List<Map> items = (List<Map>) response.get("items");

        return items.stream().map(item -> {
            Map track = (Map) item.get("track");
            String playedAtStr = (String) item.get("played_at");
            
            return new ScrobbleRecord(
                (String) track.get("id"),
                (Integer) track.get("duration_ms"),
                LocalDateTime.ofInstant(Instant.parse(playedAtStr), ZoneId.systemDefault())
            );
        }).collect(Collectors.toList());
    }
}