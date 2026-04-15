package com.devconnect.bakend.profile.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MyProfileResponse implements ProfileResponse {
    private String username;
    private String fullName;
    private String profilePicture;
    private String backgroundBanner;
    private String headLine;
    private String role;
    private String domain;
    private String about;
    private String tags;
    private boolean isPrivate;
    private boolean showStats;
    private EducationDTO education;
    private CodingProfilesDTO codingProfiles;
    private DevProfileDTO devProfiles;
    private SocialLinksDTO socialLinks;
    private ProfileStatsDTO stats;
}