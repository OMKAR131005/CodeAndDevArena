package com.devconnect.bakend.post;

import com.devconnect.bakend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    int countByUser(User user);
}
