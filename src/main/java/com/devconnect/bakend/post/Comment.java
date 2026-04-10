package com.devconnect.bakend.post;

import com.devconnect.bakend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long commentId;

    @ManyToOne
    @JoinColumn(name="post_id")
    private  Post post ;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(length = 500,nullable = false)
    private String commentText;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
