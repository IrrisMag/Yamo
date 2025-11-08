package com.irris.yamo.dtos.auth;

import com.irris.yamo.entities.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Long expiresIn; // en secondes
}
