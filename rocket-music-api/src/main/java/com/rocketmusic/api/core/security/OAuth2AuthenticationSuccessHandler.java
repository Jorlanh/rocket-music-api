package com.rocketmusic.api.core.security;

import com.rocketmusic.api.core.domain.User;
import com.rocketmusic.api.core.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${rocketmusic.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        // Extrai o cliente autorizado que contém os tokens gerados pelo Spotify
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String spotifyId = oAuth2User.getName(); 
        String accessToken = client.getAccessToken().getTokenValue();
        
        // O Spotify pode não enviar o refresh token se o usuário já autorizou o app antes,
        // por isso precisamos checar se ele veio nulo.
        String refreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;

        // Atualiza usuário existente ou cria um novo
        User user = userRepository.findBySpotifyId(spotifyId).orElse(new User());
        user.setSpotifyId(spotifyId);
        user.setAccessToken(accessToken);
        
        if (refreshToken != null) {
            user.setRefreshToken(refreshToken);
        }
        
        userRepository.save(user);

        // Redireciona de volta para o React. A sessão já estará em um Cookie seguro (JSESSIONID)
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard");
    }
}