package com.devconnect.bakend.sharedata;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private String username;
    private String fullName;
    private String profilePicture;
}
