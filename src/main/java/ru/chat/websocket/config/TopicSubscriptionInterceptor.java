package ru.chat.websocket.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import ru.chat.websocket.permissions.Admin;

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

        // подписка на общие событие
        if (array.length == 3) {
            if (array[2].equals("common"))
                return true;
            if (array[2].equals("user"))
                return true;
            if (array[2].equals(Admin.token))
                return true;
        }
        return false;
    }
}
