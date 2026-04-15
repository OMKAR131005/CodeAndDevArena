package com.devconnect.bakend.profile.dto;

import lombok.*;

@Setter@Getter@NoArgsConstructor@AllArgsConstructor@Builder
public class UpdateEducation {
    private String educationDegree;
    private Double cgpa;
    private String institution;
}
