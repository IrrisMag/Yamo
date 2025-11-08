package com.irris.yamo.security;

import com.irris.yamo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Chercher par username, email ou téléphone
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé: " + username));
    }

    /**
     * Charge un utilisateur par son ID
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec ID: " + id));
    }
}
