package com.devconnect.bakend.profile;

import com.devconnect.bakend.profile.dto.*;
import com.devconnect.bakend.sharedata.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
private final ProfileService profileService;
    // GET /api/profile/{username}
    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getProfile(username));
    }

    // GET /api/profile/me
    @GetMapping("/me")
    public  ResponseEntity<ProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    // PUT /api/profile/me/basic
    @PutMapping("/me/basic")
    public  ResponseEntity<ProfileResponse>  updateBasicProfile(@RequestBody UpdateBasicInfoRequest request) {
        return ResponseEntity.ok(profileService.updateBasicInfo(request));
    }

    // PUT /api/profile/me/privacy
    @PutMapping("/me/privacy")
    public ResponseEntity<String> updatePrivacy() {
        return ResponseEntity.ok(profileService.togglePrivacy());
    }

    // PUT /api/profile/me/education
    @PutMapping("/me/education")
    public ResponseEntity<MyProfileResponse> updateEducation(@RequestBody UpdateEducation education) {
        return ResponseEntity.ok(profileService.updateEducation(education));
    }

    // PUT /api/profile/me/social
    @PutMapping("/me/social")
    public ResponseEntity<MyProfileResponse> updateSocial(@RequestBody UpdateSocialRequest request) {
       return ResponseEntity.ok( profileService.updateSocial(request));
    }

    // PUT /api/profile/me/dev
    @PutMapping("/me/dev")
    public ResponseEntity<MyProfileResponse> updateDevProfile(@RequestBody UpdateDevProfileRequest request) {
        return ResponseEntity.ok( profileService.updateDevProfiles(request));
    }

    // GET /api/profile/{username}/followers
    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowers(@PathVariable String username,@RequestParam int page,@RequestParam int size) {
        return ResponseEntity.ok(profileService.getFollower(size,page,username));
    }

    // GET /api/profile/{username}/following
    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowing(@PathVariable String username, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(profileService.getFollowing(size,page,username));
    }
    @PostMapping("/me/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) throws IOException, IOException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String imageUrl = profileService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/profiles")
    public ResponseEntity<Page<MyProfileResponse>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String filter) {
        return ResponseEntity.ok(profileService.getAllProfiles(page, size, search, filter));
    }
}
