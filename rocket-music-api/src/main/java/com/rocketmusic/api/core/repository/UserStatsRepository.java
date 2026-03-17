package com.rocketmusic.api.core.repository;

import com.rocketmusic.api.core.dto.UserStatsDto;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserStatsRepository {
    private final JdbcTemplate jdbcTemplate;

    // Lendo direto da View Materializada via JDBC
    public UserStatsDto findStatsBySpotifyId(String spotifyId) {
        String sql = "SELECT mv.user_id, mv.total_minutes, mv.total_plays " +
                     "FROM mv_user_stats mv " +
                     "JOIN users u ON u.id = mv.user_id " +
                     "WHERE u.spotify_id = ?";
        
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserStatsDto(
            rs.getObject("user_id", UUID.class),
            rs.getLong("total_minutes"),
            rs.getLong("total_plays")
        ), spotifyId);
    }
}