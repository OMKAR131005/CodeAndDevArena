package com.devconnect.bakend.profile.dto;
import com.devconnect.bakend.sharedata.UserSummaryDTO;
import lombok.*;
import org.springframework.data.domain.Page;
import com.devconnect.bakend.post.Post;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PublicProfileResponse implements ProfileResponse{
    private String username;
    private BasicInfoDTO basicInfo;
    private EducationDTO education;
    private CodingProfilesDTO codingProfiles;
    private DevProfileDTO devProfiles;
    private SocialLinksDTO socialLinks;
    private ProfileStatsDTO stats;
    //private List<UserSummaryDTO> followers;
    //private List<UserSummaryDTO> following;
    //private Page<Post> posts;
}
