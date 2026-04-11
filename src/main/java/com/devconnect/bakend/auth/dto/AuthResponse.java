package com.devconnect.bakend.auth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthResponse {
    private String username;
    private String message;
    private String profilePicture;
    private Long userId;
}
