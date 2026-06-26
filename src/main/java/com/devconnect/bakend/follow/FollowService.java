package com.devconnect.bakend.follow;

import com.devconnect.bakend.event.FollowEvent;
import com.devconnect.bakend.notification.NotificationProducer;
import com.devconnect.bakend.notification.NotificationRepository;
import com.devconnect.bakend.notification.NotificationService;
import com.devconnect.bakend.notification.NotificationType;
import com.devconnect.bakend.profile.Profile;
import com.devconnect.bakend.profile.ProfileRepository;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import com.devconnect.bakend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FollowService {
   private final FollowRepository followRepository;
   private final UserRepository userRepository;
   private final ProfileRepository profileRepository;
   private final NotificationService notificationService;
   private final NotificationProducer  notificationProducer;
   public String toggleFollow(String username){
       User user2=userRepository.findByUsername(username);
       if(user2==null){
           throw new UsernameNotFoundException(username);
       }
       Long id=(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       User user1=userRepository.findById(id).orElse(null);
       if(followRepository.existsByFollowerAndFollowing(user1,user2)){
           Follow follow=followRepository .findByFollowerAndFollowing(user1,user2);
           followRepository.delete(follow);
           return "Unfollowed";
       }
       else{
           Profile profile=profileRepository.findByUser(user2);
           Follow follow=Follow.builder().follower(user1).following(user2).build();
           if(profile.isPrivate()){
               follow.setStatus(FollowStatus.PENDING);
           }else{
               follow.setStatus(FollowStatus.FOLLOWING);
           }
           followRepository.save(follow);
          // notificationService.createNotification(
            //       user2, user1, NotificationType.FOLLOW, null,
              //     user1.getUsername() + " started following you"
           //);
           if (!user1.getUserId().equals(user2.getUserId())) {
               FollowEvent event = new FollowEvent(id, user1.getUsername(), user2.getUserId());
               notificationProducer.sendFollowEvent(event);
           }
           return "Followed";
       }

   }

   public String getFollowStatus(String username){
       User user2=userRepository.findByUsername(username);
       if(user2==null){
            throw new UsernameNotFoundException(username);
       }
       Long id=(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       User user1=userRepository.findById(id).orElse(null);
       if(followRepository.existsByFollowerAndFollowing(user1,user2)){
           Follow follow =followRepository.findByFollowerAndFollowing(user1,user2);
           return follow.getStatus().toString();
       }
       return "No relationship found";
   }

}
