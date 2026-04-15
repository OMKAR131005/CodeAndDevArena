package com.devconnect.bakend.profile.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class UpdateBasicInfoRequest {
    private String fullName;
    private String headLine;
    private String role;
    private String domain;
    private String about;
    private String tags;
    private Boolean isPrivate;
    private Boolean showStats;
}
