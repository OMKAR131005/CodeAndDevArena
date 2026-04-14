package com.devconnect.bakend.profile.dto;

import com.devconnect.bakend.post.Post;
import lombok.*;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PrivateProfileResponse  implements ProfileResponse{
   private String username;
   private String profilePicture;
   private String backgroundBanner;
   private long followerCount;
   private long followingCount;
   private long postCount;
   //private List<Post> posts; // PUBLIC visibility only
}
