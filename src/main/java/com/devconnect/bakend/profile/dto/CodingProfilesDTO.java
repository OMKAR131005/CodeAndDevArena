

package com.devconnect.bakend.profile.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CodingProfilesDTO {
    private String leetcodeHandle;
    private String codeforcesHandle;
    private String gfgHandle;
    private String hackerRank;
    private String atcoderHandle;
    private String codechefHandle;
}
