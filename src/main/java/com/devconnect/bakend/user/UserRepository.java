package com.devconnect.bakend.user;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

     User findByEmail(String Email);

    User findByUsername(@NotEmpty String username);
}
