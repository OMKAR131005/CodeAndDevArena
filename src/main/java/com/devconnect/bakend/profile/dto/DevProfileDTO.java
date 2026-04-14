package com.devconnect.bakend.profile.dto;



import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DevProfileDTO {
    private String githubHandle;
    private String gitlabHandle;
    private String portfolioUrl;
}