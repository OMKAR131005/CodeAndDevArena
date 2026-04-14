package com.devconnect.bakend.profile.dto;


import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EducationDTO {
    private String educationDegree;
    private Double cgpa;
    private String institution;
}

