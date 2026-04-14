package com.devconnect.bakend.profile.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BasicInfoDTO {
    private String fullName;
    private String headLine;
    private String role;
    private String domain;
    private String about;
    private String tags;
    private boolean isPrivate;
    private boolean showStats;
    private String profilePicture;
    private String backgroundBanner;
}