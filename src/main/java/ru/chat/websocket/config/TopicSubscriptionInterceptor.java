package ru.chat.websocket.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import ru.chat.websocket.model.Session;
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
        Session session = (Session) headerAccessor.getSessionAttributes().get("session");
        String[] array = destination.split("/");

        if (array.length == 4 && array[2].equals(Integer.toString(session.getId()))) {
            if (array[3].equals("common"))
                return true;
            if (array[3].equals("user"))
                return true;
            if (array[3].equals(Admin.token))
                return true;
        }
        return false;
    }
}
