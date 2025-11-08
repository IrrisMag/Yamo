package com.irris.yamo.security;

import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extraire le token JWT de la requête
            String jwt = getJwtFromRequest(request);

            // Valider et authentifier
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // Charger l'utilisateur
                UserYamo user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                // Créer l'authentification
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                user, 
                                null, 
                                user.getAuthorities()
                        );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Utilisateur authentifié: {} avec rôle: {}", user.getUsername(), user.getRole());
            }
        } catch (Exception ex) {
            log.error("Impossible d'authentifier l'utilisateur", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT du header Authorization
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
