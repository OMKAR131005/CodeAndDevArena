package com.devconnect.bakend.profile.dto;

import lombok.*;

@Setter
@Getter@Builder@NoArgsConstructor@AllArgsConstructor
public class UpdateSocialRequest {
    private String socialLinkedin;
    private String socialEmail;
    private String socialYoutube;
    private String socialDiscord;
    private String socialStackoverflow;
    private String socialFacebook;
    private String socialInstagram;
    private String socialTwitterx;
    private String socialTelegram;
    private String socialOthers;
}
