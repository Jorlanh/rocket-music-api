package com.rocketmusic.api.worker.service;

import com.rocketmusic.api.core.domain.User;
import com.rocketmusic.api.core.repository.UserRepository;
import com.rocketmusic.api.worker.integration.SpotifyApiClient;
import com.rocketmusic.api.worker.dto.ScrobbleRecord;
import lombok.RequiredArgsConstructor;
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
public class ScrobblerEngine {

    private final UserRepository userRepository;
    private final SpotifyApiClient spotifyClient;
    private final JdbcTemplate jdbcTemplate;

    @Async
    @Scheduled(fixedDelayString = "${scrobbler.interval.ms:60000}")
    public void processScrobbles() {
        List<User> activeUsers = userRepository.findAll(); // Em produção, usar paginação (Pageable)
        
        for (User user : activeUsers) {
            try {
                // 1. Renovar token se necessário
                String validToken = spotifyClient.refreshUserToken(user.getRefreshToken());
                
                // 2. Buscar 'recently played'
                List<ScrobbleRecord> recentPlays = spotifyClient.getRecentlyPlayed(validToken);
                
                // 3. Batch Insert para evitar gargalos no banco
                if (!recentPlays.isEmpty()) {
                    saveScrobblesBatch(user.getId(), recentPlays);
                }
                
            } catch (Exception e) {
                // Logar falha de um usuário sem derrubar o loop dos outros
            }
        }
    }

    private void saveScrobblesBatch(UUID userId, List<ScrobbleRecord> scrobbles) {
        String sql = "INSERT INTO play_history (id, user_id, track_id, duration_ms, played_at) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        
        jdbcTemplate.batchUpdate(sql, scrobbles, scrobbles.size(),
            (PreparedStatement ps, ScrobbleRecord record) -> {
                ps.setObject(1, UUID.randomUUID());
                ps.setObject(2, userId);
                ps.setString(3, record.trackId());
                ps.setInt(4, record.durationMs());
                ps.setTimestamp(5, Timestamp.valueOf(record.playedAt()));
            });
    }
}