package ru.chat.websocket.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.chat.controller.WebSocket;
import ru.chat.utils.Time;
import ru.chat.websocket.model.UserEvent;
import ru.chat.websocket.permissions.Admin;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
         //
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        UserEvent userEvent = new UserEvent();
        int sessionId = WebSocket.getSessionId(headerAccessor);

        userEvent.setDate(Time.getNowDate());
        try {
            userEvent.setRole(headerAccessor.getSessionAttributes().get("role").toString());
        }
        catch (Exception e) {
            return ;
        }
        userEvent.setType("REMOVE");
        userEvent.setName(headerAccessor.getSessionAttributes().get("name").toString());

        // юзеру больше не приходят сообщения о том, что кто-то отключился
        //simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + "user", userEvent);

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + Admin.token, userEvent);
    }
}
