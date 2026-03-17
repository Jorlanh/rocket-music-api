-- V1__init_schema.sql

-- 1. Tabela de Usuários
CREATE TABLE users (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) UNIQUE NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    user_status VARCHAR(100),
    instagram_handle VARCHAR(50),
    twitter_handle VARCHAR(50)
);

-- 2. Tabela de Conquistas (AI)
CREATE TABLE user_achievements (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    milestone_type VARCHAR(100) NOT NULL,
    badge_url TEXT NOT NULL
);

-- 3. Tabela Particionada para Big Data (Scrobbling)
CREATE TABLE play_history (
    id UUID,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    track_id VARCHAR(255) NOT NULL,
    duration_ms INT NOT NULL,
    played_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id, played_at)
) PARTITION BY RANGE (played_at);

-- 4. Partição inicial para o ano corrente (2024-2026)
CREATE TABLE play_history_2024_2026 PARTITION OF play_history 
    FOR VALUES FROM ('2024-01-01') TO ('2027-01-01');

-- 5. View Materializada para Estatísticas Instantâneas
CREATE MATERIALIZED VIEW mv_user_stats AS
SELECT 
    user_id,
    SUM(duration_ms) / 60000 AS total_minutes,
    COUNT(track_id) AS total_plays
FROM play_history
GROUP BY user_id;

-- Índice único para permitir Refresh Concorrente sem travar consultas
CREATE UNIQUE INDEX idx_mv_user_stats_user_id ON mv_user_stats(user_id);