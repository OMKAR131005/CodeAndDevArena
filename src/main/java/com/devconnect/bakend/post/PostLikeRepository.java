package com.devconnect.bakend.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
}
