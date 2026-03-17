-- V1__init_schema.sql DEFINITIVO

-- 1. ESTRUTURA CORE (Usuários e Amizades)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    spotify_id VARCHAR(255) UNIQUE NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    user_status VARCHAR(100),
    instagram_handle VARCHAR(50),
    twitter_handle VARCHAR(50)
);

CREATE TABLE friendships (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    friend_id UUID REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, friend_id)
);

-- 2. GAMIFICAÇÃO E IA (Google Gemini)
CREATE TABLE user_achievements (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    milestone_type VARCHAR(100) NOT NULL,
    badge_url TEXT NOT NULL
);

-- 3. SOCIAL E MENSAGENS
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    album_id VARCHAR(255) NOT NULL,
    rating DECIMAL(2,1) CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE direct_messages (
    id UUID PRIMARY KEY,
    sender_id UUID REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. BIG DATA (Histórico Particionado)
CREATE TABLE play_history (
    id UUID,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    track_id VARCHAR(255) NOT NULL,
    track_name VARCHAR(255),
    artist_name VARCHAR(255),
    album_name VARCHAR(255),
    duration_ms INT NOT NULL,
    played_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id, played_at)
) PARTITION BY RANGE (played_at);

-- Partições Iniciais (Escalabilidade)
CREATE TABLE play_history_2024 PARTITION OF play_history FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
CREATE TABLE play_history_2025 PARTITION OF play_history FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
CREATE TABLE play_history_2026 PARTITION OF play_history FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');
CREATE TABLE play_history_default PARTITION OF play_history DEFAULT;

-- 5. ÍNDICES DE PERFORMANCE
CREATE INDEX idx_play_history_user_id ON play_history(user_id);
CREATE INDEX idx_friendships_user_id ON friendships(user_id);

-- 6. VIEWS MATERIALIZADAS (Estatísticas Instantâneas)
CREATE MATERIALIZED VIEW mv_user_stats AS
SELECT 
    user_id,
    SUM(duration_ms) / 60000 AS total_minutes,
    COUNT(track_id) AS total_plays
FROM play_history 
GROUP BY user_id;

CREATE MATERIALIZED VIEW mv_top_artists AS
SELECT 
    user_id,
    artist_name,
    COUNT(*) as play_count
FROM play_history
GROUP BY user_id, artist_name;

-- Índices Únicos (Necessários para Refresh Concorrente)
CREATE UNIQUE INDEX idx_mv_stats_uid ON mv_user_stats(user_id);
CREATE UNIQUE INDEX idx_mv_artists_uid ON mv_top_artists(user_id, artist_name);