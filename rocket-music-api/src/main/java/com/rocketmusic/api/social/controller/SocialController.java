package com.rocketmusic.api.social.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller de Elite para o Ecossistema Social do Rocket Music.
 * Gerencia o Feed de Atividades baseado no Spotify e o Grafo de Amizades.
 */
@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Segue um usuário. Cria a conexão de amizade no banco de dados.
     * @param principal Usuário logado via Spotify.
     * @param friendId UUID do usuário a ser seguido.
     */
    @PostMapping("/follow/{friendId}")
    public ResponseEntity<Void> follow(
            @AuthenticationPrincipal OAuth2User principal, 
            @PathVariable UUID friendId) {
        
        String spotifyId = principal.getName();
        
        String sql = """
            INSERT INTO friendships (id, user_id, friend_id) 
            VALUES (
                ?, 
                (SELECT id FROM users WHERE spotify_id = ?), 
                ?
            ) ON CONFLICT DO NOTHING
            """;
        
        jdbcTemplate.update(sql, UUID.randomUUID(), spotifyId, friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retorna o Feed Global de Atividade dos Amigos.
     * Cruza os dados de 'play_history' com as conexões de 'friendships'.
     */
    @GetMapping("/feed")
    public List<Map<String, Object>> getFriendActivity(@AuthenticationPrincipal OAuth2User principal) {
        String spotifyId = principal.getName();

        // query otimizada para buscar scrobbles reais vindos do Worker
        String sql = """
            SELECT 
                h.id, 
                u.spotify_id as "user", 
                COALESCE(u.user_status, 'https://github.com/shadcn.png') as "avatar",
                'ouviu' as action, 
                h.track_name as target, 
                h.played_at as "timeAgo"
            FROM play_history h
            JOIN users u ON h.user_id = u.id
            JOIN friendships f ON f.friend_id = u.id
            WHERE f.user_id = (SELECT id FROM users WHERE spotify_id = ?)
            ORDER BY h.played_at DESC
            LIMIT 20
            """;

        return jdbcTemplate.queryForList(sql, spotifyId);
    }
}