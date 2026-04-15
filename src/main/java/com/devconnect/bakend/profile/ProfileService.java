package com.devconnect.bakend.profile;

import com.devconnect.bakend.exceptions.ResourceNotFoundException;
import com.devconnect.bakend.follow.Follow;
import com.devconnect.bakend.follow.FollowRepository;
import com.devconnect.bakend.follow.FollowStatus;
import com.devconnect.bakend.post.PostRepository;
import com.devconnect.bakend.profile.dto.*;
import com.devconnect.bakend.sharedata.UserSummaryDTO;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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
        Long requestingId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if(requestingId==null)return null;
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
                            .socialInstagram(profile.getSocialInstagram()).build()).backgroundBanner(profile.getBackgroundBanner()).
                    build();

        }
        return PrivateProfileResponse.builder().
                profilePicture(profile.getProfilePicture())
                .backgroundBanner(profile.getBackgroundBanner())
                .username(following.getUsername())
                .followerCount(followRepository.countByFollowingAndStatus(following,FollowStatus.FOLLOWING))
                .followingCount(followRepository.countByFollowerAndStatus(following,FollowStatus.FOLLOWING)).build();
    }

    public MyProfileResponse getMyProfile(){
        Long userId =(Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Profile profile =profileRepository.findByUser(user);
        return buildMyProfileResponse(user,profile);

    }
    public MyProfileResponse updateBasicInfo(UpdateBasicInfoRequest basicInfoRequest){
        Long userId =(Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Profile profile =profileRepository.findByUser(user);

        if(basicInfoRequest.getFullName()!=null){
            profile.setFullName(basicInfoRequest.getFullName());
        }
        if(basicInfoRequest.getTags()!=null){
            profile.setTags(basicInfoRequest.getTags());
        }
        if(basicInfoRequest.getHeadLine()!=null){
            profile.setHeadLine(basicInfoRequest.getHeadLine());
        }
        if(basicInfoRequest.getRole()!=null){
            profile.setRole(basicInfoRequest.getRole());
        }
        if(basicInfoRequest.getDomain()!=null){
            profile.setDomain(basicInfoRequest.getDomain());
        }
        if(basicInfoRequest.getAbout()!=null){
            profile.setAbout(basicInfoRequest.getAbout());
        }
        if(basicInfoRequest.getIsPrivate()!=null){
            profile.setPrivate(basicInfoRequest.getIsPrivate());
        }
        if(basicInfoRequest.getShowStats()!=null){
            profile.setShowStats(basicInfoRequest.getShowStats());
        }
        profileRepository.save(profile);
         return buildMyProfileResponse(user,profile);
    }
    public MyProfileResponse updateEducation(UpdateEducation education){
        Long userId =(Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Profile profile =profileRepository.findByUser(user);
        if(education.getEducationDegree()!=null){
            profile.setEducationDegree(education.getEducationDegree());
        }
        if(education.getCgpa()!=null){
            profile.setCgpa(education.getCgpa());
        }
        if(education.getInstitution()!=null){
            profile.setInstitution(education.getInstitution());
        }
        profileRepository.save(profile);
        return buildMyProfileResponse(user,profile);
    }

    public MyProfileResponse updateSocial(UpdateSocialRequest socialRequest){
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Profile profile = profileRepository.findByUser(user);

        if(socialRequest.getSocialEmail()!=null) profile.setSocialEmail(socialRequest.getSocialEmail());
        if(socialRequest.getSocialLinkedin()!=null) profile.setSocialLinkedin(socialRequest.getSocialLinkedin());
        if(socialRequest.getSocialYoutube()!=null) profile.setSocialYoutube(socialRequest.getSocialYoutube());
        if(socialRequest.getSocialDiscord()!=null) profile.setSocialDiscord(socialRequest.getSocialDiscord());
        if(socialRequest.getSocialStackoverflow()!=null) profile.setSocialStackoverflow(socialRequest.getSocialStackoverflow());
        if(socialRequest.getSocialFacebook()!=null) profile.setSocialFacebook(socialRequest.getSocialFacebook());
        if(socialRequest.getSocialInstagram()!=null) profile.setSocialInstagram(socialRequest.getSocialInstagram());
        if(socialRequest.getSocialTwitterx()!=null) profile.setSocialTwitterx(socialRequest.getSocialTwitterx());
        if(socialRequest.getSocialTelegram()!=null) profile.setSocialTelegram(socialRequest.getSocialTelegram());
        if(socialRequest.getSocialOthers()!=null) profile.setSocialOthers(socialRequest.getSocialOthers());

        profileRepository.save(profile);
        return buildMyProfileResponse(user, profile);
    }
    public MyProfileResponse updateDevProfiles(UpdateDevProfileRequest devRequest){
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Profile profile = profileRepository.findByUser(user);

        if(devRequest.getLeetcodeHandle()!=null) profile.setLeetcodeHandle(devRequest.getLeetcodeHandle());
        if(devRequest.getCodeforcesHandle()!=null) profile.setCodeforcesHandle(devRequest.getCodeforcesHandle());
        if(devRequest.getGfgHandle()!=null) profile.setGfgHandle(devRequest.getGfgHandle());
        if(devRequest.getHackerRank()!=null) profile.setHackerRank(devRequest.getHackerRank());
        if(devRequest.getAtcoderHandle()!=null) profile.setAtcoderHandle(devRequest.getAtcoderHandle());
        if(devRequest.getCodechefHandle()!=null) profile.setCodechefHandle(devRequest.getCodechefHandle());
        if(devRequest.getGithubHandle()!=null) profile.setGithubHandle(devRequest.getGithubHandle());
        if(devRequest.getGitlabHandle()!=null) profile.setGitlabHandle(devRequest.getGitlabHandle());
        if(devRequest.getPortfolioUrl()!=null) profile.setPortfolioUrl(devRequest.getPortfolioUrl());

        profileRepository.save(profile);
        return buildMyProfileResponse(user, profile);
    }

    @Transactional
    public String togglePrivacy(){
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Profile profile = profileRepository.findByUser(user);
        if(profile.isPrivate()){
            followRepository.updateFollowStatus(user,FollowStatus.FOLLOWING,FollowStatus.PENDING);
            profile.setPrivate(false);
            profileRepository.save(profile);
            return "Now your profile is public";
        }else{
            profile.setPrivate(true);
            profileRepository.save(profile);
            return "Now your profile is private";
        }

    }

    public Page<UserSummaryDTO> getFollower(int size){
        Pageable pageable=Pageable.ofSize(size);
        Long userId = (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Page<Follow> follower = followRepository.findByFollowerAndStatus(user, FollowStatus.FOLLOWING, pageable);
        follower.stream().map((fllwer)->toUserSummery(fllwer)).toList();

    }
    private MyProfileResponse buildMyProfileResponse(User user, Profile profile) {
        return MyProfileResponse.builder()
                .username(user.getUsername())
                .fullName(profile.getFullName())
                .profilePicture(profile.getProfilePicture())
                .backgroundBanner(profile.getBackgroundBanner())
                .headLine(profile.getHeadLine())
                .role(profile.getRole())
                .domain(profile.getDomain())
                .about(profile.getAbout())
                .tags(profile.getTags())
                .isPrivate(profile.isPrivate())
                .showStats(profile.isShowStats())
                .education(EducationDTO.builder()
                        .educationDegree(profile.getEducationDegree())
                        .cgpa(profile.getCgpa())
                        .institution(profile.getInstitution()).build())
                .codingProfiles(CodingProfilesDTO.builder()
                        .leetcodeHandle(profile.getLeetcodeHandle())
                        .codeforcesHandle(profile.getCodeforcesHandle())
                        .gfgHandle(profile.getGfgHandle())
                        .hackerRank(profile.getHackerRank())
                        .atcoderHandle(profile.getAtcoderHandle())
                        .codechefHandle(profile.getCodechefHandle()).build())
                .devProfiles(DevProfileDTO.builder()
                        .githubHandle(profile.getGithubHandle())
                        .gitlabHandle(profile.getGitlabHandle())
                        .portfolioUrl(profile.getPortfolioUrl()).build())
                .socialLinks(SocialLinksDTO.builder()
                        .socialEmail(profile.getSocialEmail())
                        .socialLinkedin(profile.getSocialLinkedin())
                        .socialYoutube(profile.getSocialYoutube())
                        .socialDiscord(profile.getSocialDiscord())
                        .socialStackoverflow(profile.getSocialStackoverflow())
                        .socialFacebook(profile.getSocialFacebook())
                        .socialInstagram(profile.getSocialInstagram())
                        .socialTwitterx(profile.getSocialTwitterx())
                        .socialTelegram(profile.getSocialTelegram())
                        .socialOthers(profile.getSocialOthers()).build())
                .stats(ProfileStatsDTO.builder()
                        .followerCount(followRepository.countByFollowingAndStatus(user, FollowStatus.FOLLOWING))
                        .followingCount(followRepository.countByFollowerAndStatus(user, FollowStatus.FOLLOWING))
                        .postCount(postRepository.countByUser(user))
                        .weeklyViews(profileViewRepository.countViews(profile, LocalDateTime.now().minusDays(7)))
                        .monthlyViews(profileViewRepository.countViews(profile, LocalDateTime.now().minusDays(30)))
                        .totalViews(profileViewRepository.totalCountViews(profile)).build())
                .build();

    }
    public UserSummaryDTO toUserSummery(Follow follower){
        User user=follower.getFollowing();
        Profile profile = profileRepository.findByUser(user);
        return UserSummaryDTO.builder().username(user.getUsername()).profilePicture(profile.getProfilePicture()).fullName(profile.getFullName()).build();
    }
}

