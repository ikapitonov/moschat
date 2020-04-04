package ru.chat.websocket.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class TopicSubscriptionInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {

            if (!validateSubscription(headerAccessor)) {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(StompHeaderAccessor headerAccessor) {
        String destination = headerAccessor.getDestination();
        String[] array = destination.split("/");
        String role = headerAccessor.getSessionAttributes().get("role").toString();

        // подписка на общие событие
        if (array.length == 3) {
            return array[2].equals(role);
        }
        // подписка на личные события
        if (array.length == 4) {
            return array[2].equals(role) && array[3].equals(headerAccessor.getSessionId());
        }
        return false;
    }
}
