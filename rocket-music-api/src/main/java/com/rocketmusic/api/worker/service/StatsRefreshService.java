package com.rocketmusic.api.worker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsRefreshService {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 300000) // Atualiza a cada 5 minutos
    @Transactional
    public void refreshMaterializedViews() {
        // Atualiza as views sem travar as leituras do usuário (CONCURRENTLY)
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_stats");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_top_artists");
    }
}