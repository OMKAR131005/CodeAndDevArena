package com.devconnect.bakend.message;

import com.devconnect.bakend.exceptions.ResourceNotFoundException;
import com.devconnect.bakend.message.dto.MessageRequest;
import com.devconnect.bakend.message.dto.MessageResponse;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final SimpUserRegistry simpUserRegistry;

    public void sendMessage(MessageRequest request,Long senderId) {
        //Long senderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        User receiver = userRepository.findByUsername(request.getReceiverUsername());
        if (receiver == null) throw new ResourceNotFoundException("receiver not found");

        String roomId = generateRoomId(senderId, receiver.getUserId());

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .messageText(request.getMessageText())
                .roomId(roomId)
                .build();
        messageRepository.save(message);

        MessageResponse response = MessageResponse.builder()
                .senderUsername(sender.getUsername())
                .receiverUsername(receiver.getUsername())
                .content(message.getMessageText())
                .roomId(roomId)
                .createdAt(message.getCreatedAt())
                .build();

        System.out.println("Sending to user: " + receiver.getUserId().toString());
        messagingTemplate.convertAndSend(
                "/topic/messages/" + receiver.getUserId(),
                response
        );
        System.out.println("Message sent to broker");

        // After convertAndSendToUser
        System.out.println("Active users in broker: ");
        simpUserRegistry.findSubscriptions(s -> true).forEach(s ->
                System.out.println("  Session: " + s.getSession().getId() +
                        " Principal: " + s.getSession().getUser()));
    }

    public Page<MessageResponse> getChatHistory(String receiverUsername, Pageable pageable) {
        Long senderId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        User receiver = userRepository.findByUsername(receiverUsername);
        if (receiver == null) throw new ResourceNotFoundException("receiver not found");

        String roomId = generateRoomId(senderId, receiver.getUserId());
        return messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(msg -> MessageResponse.builder()
                        .senderUsername(msg.getSender().getUsername())
                        .receiverUsername(msg.getReceiver().getUsername())
                        .content(msg.getMessageText())
                        .roomId(msg.getRoomId())
                        .createdAt(msg.getCreatedAt())
                        .build());
    }

    private String generateRoomId(Long id1, Long id2) {
        return Math.min(id1, id2) + "_" + Math.max(id1, id2);
    }
}
