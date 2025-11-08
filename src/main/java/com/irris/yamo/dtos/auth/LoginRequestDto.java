package com.irris.yamo.dtos.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    private String username;  // Peut être username, email ou téléphone
    private String password;
}
