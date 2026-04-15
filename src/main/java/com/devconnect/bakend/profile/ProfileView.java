package com.devconnect.bakend.profile;

import com.devconnect.bakend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="views" ,uniqueConstraints ={@UniqueConstraint(columnNames = {"profile_id","viewer_id"})})
public class ProfileView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @JoinColumn(name="viewer_id")
    @ManyToOne
    private User viewer;

    @JoinColumn(name="profile_id")
    @ManyToOne
    private Profile profile;

    private LocalDateTime lastViewAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

