package com.devconnect.bakend.profile;

import com.devconnect.bakend.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ProfileViewRepository extends JpaRepository<ProfileView,Long> {
    ProfileView findByViewerAndProfile(User user,Profile profile);

    @Query("SELECT COUNT(pv) FROM ProfileView pv WHERE pv.profile = :profile AND pv.lastViewAt >= :date")
    int countViews(@Param("profile") Profile profile, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(pv) FROM ProfileView pv WHERE pv.profile = :profile")
    int totalCountViews(@Param("profile") Profile profile);
}
