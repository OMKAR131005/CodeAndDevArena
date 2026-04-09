package com.devconnect.bakend.profile;

import com.devconnect.bakend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    private String profilePicture;

    private String backgroundBanner;

    private String headLine;
    private String role;
    private String domain;
    private String about;
    private String tags;
    @Column(name="is_private",columnDefinition = "boolean default false")
    private boolean isPrivate;
    @Column(name="show_stats",columnDefinition = "boolean default false")
    private boolean showStats;

    // education
    private String educationDegree;
    private Double cgpa;
    private String institution;

    //competitive profile;
    private String leetcodeHandle;
    private String codeforcesHandle;
    private String gfgHandle;
    private String hackerRank;
    private String atcoderHandle;
    private String codechefHandle;

    //development profiles:
    private String githubHandle;
    private String gitlabHandle;
    private String portfolioUrl;

    // all socials:
    private String socialLinkedin;
    private String socialEmail;
    private String socialYoutube;
    private String socialDiscord;
    private String socialStackoverflow;
    private String socialFacebook;
    private String socialInstagram;
    private String socialTwitterx;
    private String socialTelegram;
    private String socialOthers;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
