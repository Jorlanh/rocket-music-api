package com.rocketmusic.api.worker.service;

import com.rocketmusic.api.core.domain.User;
import com.rocketmusic.api.core.repository.UserRepository;
import com.rocketmusic.api.worker.dto.ScrobbleRecord;
import com.rocketmusic.api.worker.integration.SpotifyApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrobblerEngine {

    private final UserRepository userRepository;
    private final SpotifyApiClient spotifyClient;
    private final JdbcTemplate jdbcTemplate;

    @Async
    @Scheduled(fixedDelay = 60000) // Roda a cada 1 minuto
    public void processScrobbles() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getRefreshToken() == null) continue;

            try {
                String newToken = spotifyClient.refreshUserToken(user.getRefreshToken());
                user.setAccessToken(newToken);
                userRepository.save(user);

                List<ScrobbleRecord> recentPlays = spotifyClient.getRecentlyPlayed(newToken);
                saveScrobblesBatch(user.getId(), recentPlays);
                
                log.info("Scrobbles atualizados para o usuário: {}", user.getSpotifyId());
            } catch (Exception e) {
                log.error("Erro ao processar scrobbles para {}: {}", user.getSpotifyId(), e.getMessage());
            }
        }
    }

    private void saveScrobblesBatch(UUID userId, List<ScrobbleRecord> scrobbles) {
        String sql = "INSERT INTO play_history (id, user_id, track_id, duration_ms, played_at) " +
                     "VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";

        jdbcTemplate.batchUpdate(sql, scrobbles, 50,
            (PreparedStatement ps, ScrobbleRecord record) -> {
                ps.setObject(1, UUID.randomUUID());
                ps.setObject(2, userId);
                ps.setString(3, record.trackId());
                ps.setInt(4, record.durationMs());
                ps.setTimestamp(5, Timestamp.valueOf(record.playedAt()));
            });
    }
}