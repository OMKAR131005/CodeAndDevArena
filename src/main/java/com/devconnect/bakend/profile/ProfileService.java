package com.devconnect.bakend.profile;

import com.devconnect.bakend.exceptions.ResourceNotFoundException;
import com.devconnect.bakend.follow.Follow;
import com.devconnect.bakend.follow.FollowRepository;
import com.devconnect.bakend.follow.FollowStatus;
import com.devconnect.bakend.post.PostRepository;
import com.devconnect.bakend.profile.dto.*;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ProfileService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private  final PostRepository postRepository;
    private final ProfileViewRepository profileViewRepository;
    public ProfileResponse getProfile(String profileUsername){
        Long requestingId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User following=userRepository.findByUsername(profileUsername);
        if(following==null)throw new ResourceNotFoundException("user with username:"+profileUsername+"not Found");
        Profile profile=profileRepository.findByUser(following);
        if(profile==null)throw new ResourceNotFoundException("profile for username:"+profileUsername+"not Found");
        Optional<User>optionalFollower=userRepository.findById(requestingId);
        optionalFollower.orElseThrow(()->new ResourceNotFoundException("user with userId:"+requestingId+"not Found"));
        User follower=optionalFollower.get();
        ProfileView profileView=profileViewRepository.findByViewerAndProfile(follower,profile);
        if(!following.getUserId().equals(requestingId)) {
            if (profileView == null) {
                profileView = ProfileView.builder().viewer(follower).profile(profile).lastViewAt(LocalDateTime.now()).build();
                profileViewRepository.save(profileView);
            } else {
                profileView.setLastViewAt(LocalDateTime.now());
                profileViewRepository.save(profileView);
            }
        }
        boolean isFollow=followRepository.existsByFollowerAndFollowingAndStatus(follower,following, FollowStatus.FOLLOWING);
        if(isFollow||!profile.isPrivate()||following.getUserId().equals(requestingId)){
           return PublicProfileResponse.builder().username(following.getUsername())

                    .codingProfiles(CodingProfilesDTO.builder().atcoderHandle(profile.getAtcoderHandle())
                            .codechefHandle(profile.getCodechefHandle()).
                            codeforcesHandle(profile.getCodeforcesHandle())
                            .leetcodeHandle(profile.getLeetcodeHandle()).build())


                    .basicInfo(BasicInfoDTO.builder().
                            profilePicture(profile.getProfilePicture())
                            .about(profile.getAbout()).domain(profile.getDomain()).
                            role(profile.getRole()).build()).

                    stats(ProfileStatsDTO.builder()
                            .followingCount(followRepository.countByFollowerAndStatus(following,FollowStatus.FOLLOWING))
                            .followerCount(followRepository.countByFollowingAndStatus(following ,FollowStatus.FOLLOWING))
                            .postCount(postRepository.countByUser(profile.getUser()))
                            .monthlyViews(profileViewRepository.countViews(profile,LocalDateTime.now().minusDays(30))).
                            totalViews(profileViewRepository.totalCountViews(profile))
                            .weeklyViews(profileViewRepository.countViews(profile,LocalDateTime.now().minusDays(7))).build())

                    .devProfiles(DevProfileDTO.builder()
                            .githubHandle(profile.getGithubHandle())
                            .gitlabHandle(profile.getGitlabHandle())
                            .portfolioUrl(profile.getPortfolioUrl()).build())
                    .education(EducationDTO.builder()
                            .cgpa(profile.getCgpa()).
                            educationDegree(profile.getEducationDegree())
                            .institution(profile.getInstitution()).build())
                    .socialLinks(SocialLinksDTO.builder()
                            .socialDiscord(profile.getSocialDiscord())
                            .socialEmail(profile.getSocialEmail())
                            .socialFacebook(profile.getSocialFacebook())
                            .socialInstagram(profile.getSocialInstagram()).build()).
                    build();

        }
        return PrivateProfileResponse.builder().
                profilePicture(profile.getProfilePicture())
                .backgroundBanner(profile.getBackgroundBanner())
                .username(following.getUsername())
                .followerCount(followRepository.countByFollowingAndStatus(following,FollowStatus.FOLLOWING))
                .followingCount(followRepository.countByFollowerAndStatus(following,FollowStatus.FOLLOWING)).build();
    }
}
