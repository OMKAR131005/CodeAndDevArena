package com.devconnect.bakend.follow;

import com.devconnect.bakend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    boolean existsByFollowerAndFollowingAndStatus(User follower,User following,FollowStatus followStatus);
    Page<Follow> findByFollowerAndStatus(User follower, FollowStatus status, Pageable pageable);


    Page<Follow> findByFollowingAndStatus(User following, FollowStatus status,Pageable pageable);
    List<Follow> findByFollowerAndStatus(User follower, FollowStatus status);


    List<Follow> findByFollowingAndStatus(User following, FollowStatus status);
    int countByFollowingAndStatus(User following ,FollowStatus status);
    int countByFollowerAndStatus(User follower ,FollowStatus status);
    @Modifying@Transactional
    @Query("UPDATE Follow f SET f.status = :newStatus WHERE f.following = :user AND f.status = :oldStatus")
    void updateFollowStatus(@Param("user") User user, @Param("newStatus") FollowStatus newStatus, @Param("oldStatus") FollowStatus oldStatus);
}
