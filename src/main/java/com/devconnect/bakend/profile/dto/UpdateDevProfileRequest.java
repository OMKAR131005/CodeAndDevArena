package com.devconnect.bakend.profile.dto;

import lombok.*;

@Setter@Getter@Builder@NoArgsConstructor@AllArgsConstructor
public class UpdateDevProfileRequest {
    private String githubHandle;
    private String gitlabHandle;
    private String portfolioUrl;
    private String leetcodeHandle;
    private String codeforcesHandle;
    private String gfgHandle;
    private String hackerRank;
    private String atcoderHandle;
    private String codechefHandle;
}
