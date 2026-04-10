package com.devconnect.bakend.message;

import com.devconnect.bakend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long messageId;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    private String messageText;

    private String imagePublicId;

    private String imageUrl;

    @Column(nullable = false)
    private String roomId;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private  LocalDateTime updatedAt;
}
