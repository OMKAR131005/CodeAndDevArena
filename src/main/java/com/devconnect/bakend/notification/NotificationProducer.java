package com.devconnect.bakend.notification;

import com.devconnect.bakend.config.RabbitMQConfig;
import com.devconnect.bakend.event.CommentEvent;
import com.devconnect.bakend.event.FollowEvent;
import com.devconnect.bakend.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Jackson2JsonMessageConverter messageConverter;

    public void sendLikeEvent(LikeEvent event) {
        log.info("Publishing LikeEvent for post {}", event.getPostId());
        MessageProperties props = new MessageProperties();
        props.setHeader("eventType", "LIKE");
        Message message = messageConverter.toMessage(event, props);
        rabbitTemplate.send(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY_LIKE, message);
    }

    public void sendCommentEvent(CommentEvent event) {
        log.info("Publishing CommentEvent for post {}", event.getPostId());
        MessageProperties props = new MessageProperties();
        props.setHeader("eventType", "COMMENT");
        Message message = messageConverter.toMessage(event, props);
        rabbitTemplate.send(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY_COMMENT, message);
    }

    public void sendFollowEvent(FollowEvent event) {
        log.info("Publishing FollowEvent from user {}", event.getActorUsername());
        MessageProperties props = new MessageProperties();
        props.setHeader("eventType", "FOLLOW");
        Message message = messageConverter.toMessage(event, props);
        rabbitTemplate.send(RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.ROUTING_KEY_FOLLOW, message);
    }
}