package ru.chat.websocket.permissions;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class IsAuthorization {

    // на всякий случай
    public static boolean validate(SimpMessageHeaderAccessor headerAccessor) {
        try {
            return !(headerAccessor.getSessionAttributes().get("role").toString().isEmpty());
        }
        catch (Exception e) {
            return false;
        }
    }
}
