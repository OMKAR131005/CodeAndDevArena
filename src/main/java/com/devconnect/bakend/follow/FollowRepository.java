package com.devconnect.bakend.follow;

import com.devconnect.bakend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    boolean existsByFollowerAndFollowingAndStatus(User follower,User following,FollowStatus followStatus);
    // Gets all people that 'user' is following
    List<Follow> findByFollowerAndStatus(User follower, FollowStatus status);

    // Gets all followers of 'user'
    List<Follow> findByFollowingAndStatus(User following, FollowStatus status);
    int countByFollowingAndStatus(User following ,FollowStatus status);
    int countByFollowerAndStatus(User follower ,FollowStatus status);
}
