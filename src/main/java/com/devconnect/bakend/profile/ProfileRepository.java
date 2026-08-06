package com.devconnect.bakend.profile;

import com.devconnect.bakend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

    Profile findByUser(User user);

    void deleteByUser(User user);

    Optional<Profile> findByUser_UserId(Long userId);
    @Query("""
    SELECT p FROM Profile p
    WHERE (
        :search = '' OR
        LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.user.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.role) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.domain) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.headLine) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.tags) LIKE LOWER(CONCAT('%', :search, '%'))
    )
    AND (
        :filter = '' OR
        (:filter = 'CP_DSA' AND (
            p.leetcodeHandle IS NOT NULL OR p.codeforcesHandle IS NOT NULL OR
            p.gfgHandle IS NOT NULL OR p.hackerRank IS NOT NULL OR
            p.atcoderHandle IS NOT NULL OR p.codechefHandle IS NOT NULL OR
            LOWER(p.tags) LIKE '%dsa%' OR LOWER(p.tags) LIKE '%leetcode%' OR
            LOWER(p.tags) LIKE '%codeforces%' OR LOWER(p.tags) LIKE '%gfg%'
        )) OR
        (:filter = 'DEVS' AND (
            p.githubHandle IS NOT NULL OR p.gitlabHandle IS NOT NULL OR
            p.portfolioUrl IS NOT NULL OR
            LOWER(p.tags) LIKE '%developer%' OR LOWER(p.tags) LIKE '%react%' OR
            LOWER(p.tags) LIKE '%java%' OR LOWER(p.tags) LIKE '%node%' OR
            LOWER(p.role) LIKE '%developer%' OR LOWER(p.role) LIKE '%engineer%'
        )) OR
        (:filter = 'Experienced' AND (
            LOWER(p.role) LIKE '%senior%' OR LOWER(p.role) LIKE '%lead%' OR
            LOWER(p.role) LIKE '%manager%' OR LOWER(p.role) LIKE '%principal%' OR
            LOWER(p.role) LIKE '%staff%' OR LOWER(p.tags) LIKE '%senior%' OR
            LOWER(p.tags) LIKE '%experienced%'
        )) OR
        (:filter = 'Professionals' AND (
            LOWER(p.role) LIKE '%sde%' OR LOWER(p.role) LIKE '%engineer%' OR
            LOWER(p.tags) LIKE '%professional%' OR LOWER(p.tags) LIKE '%working%' OR
            LOWER(p.tags) LIKE '%company%'
        )) OR
        (:filter = 'Trending' AND p.isPrivate = false)
    )
    ORDER BY p.profileId DESC
    """)
    Page<Profile> findBySearchAndFilter(
            @Param("search") String search,
            @Param("filter") String filter,
            Pageable pageable
    );

}
