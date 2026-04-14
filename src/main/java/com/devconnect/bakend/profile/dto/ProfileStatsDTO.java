package com.devconnect.bakend.profile.dto;



import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileStatsDTO {
    private long followerCount;
    private long followingCount;
    private long postCount;
    private long weeklyViews;
    private long monthlyViews;
    private long totalViews;
}