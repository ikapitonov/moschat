package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import ru.chat.auth.AuthData;
import ru.chat.repositories.ClientCommentRepo;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.utils.Html;
import ru.chat.utils.Time;
import ru.chat.websocket.model.*;
import ru.chat.websocket.permissions.Admin;


@Controller
public class WebSocket {
    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private ClientCommentRepo clientCommentRepo;

    @MessageMapping("/chat.userWrite")
    public void userWrite(SimpMessageHeaderAccessor headerAccessor) {
        UserWrite user = new UserWrite();
        int sessionId = getSessionId(headerAccessor);

        user.setUsername(headerAccessor.getSessionAttributes().get("name").toString());
        user.setType("WRITE");
        user.setSession(headerAccessor.getSessionId());
        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/common", user);
    }

    @MessageMapping("/chat.addUser")
    public void addUser (SimpMessageHeaderAccessor headerAccessor, @Payload AuthData authData) {
        UserEvent userEvent = new UserEvent();
        int sessionId = getSessionId(headerAccessor);

        if (!authData.isStatus())
            return ;

        if (authData.getRole().equals("user")) {
            if (authData.getUser() == null || authData.getUser().getId() == 0)
                return ;

            headerAccessor.getSessionAttributes().put("appUser", authData.getUser());
            headerAccessor.getSessionAttributes().put("name", authData.getName());
            headerAccessor.getSessionAttributes().put("role", authData.getRole());

            if (authData.getEmail() != null && !authData.getEmail().isEmpty()) {
                headerAccessor.getSessionAttributes().put("email", authData.getEmail());
            }
            if (authData.getPhone() != null)  {
                headerAccessor.getSessionAttributes().put("phone", authData.getPhone());
            }
        }
        else {
            if (authData.getToken() != null && !authData.getToken().isEmpty() && authData.getToken().equals(Admin.token)) {
                headerAccessor.getSessionAttributes().put("appUser", authData.getUser());
                headerAccessor.getSessionAttributes().put("role", authData.getRole());
                headerAccessor.getSessionAttributes().put("name", Admin.name);
            }
            else
                return ;
        }
        userEvent.setDate(Time.getNowDate());
        userEvent.setRole(headerAccessor.getSessionAttributes().get("role").toString());
        userEvent.setType("ADD");
        userEvent.setName(headerAccessor.getSessionAttributes().get("name").toString());

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + "user", userEvent);
        simpMessagingTemplate.convertAndSend("/topic/"  + sessionId + "/" + Admin.token, userEvent);
    }

    @MessageMapping("/chat.sendMessage")
    public void SendMessage (SimpMessageHeaderAccessor headerAccessor, @Payload Message message) {
        ClientMessage clientMessage = new ClientMessage();
        int sessionId = getSessionId(headerAccessor);
        String content;

        message.setContent(Html.decodeParseLines(message.getContent()));
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ;
        }
        clientMessage.setSession((Session) headerAccessor.getSessionAttributes().get("session"));

        content = message.getContent().trim();
        if (content.length() >= 2000)
            content = content.substring(0, 1996) + "...";

        clientMessage.setAppUser(AppUser.duplicate((AppUser) headerAccessor.getSessionAttributes().get("appUser")));
        clientMessage.setContent(content);
        clientMessage.setType("MESSAGE");

        clientMessage = clientMessageRepo.save(clientMessage);

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + Admin.token, clientMessage);

        clientMessage.getAppUser().setFields(null);
        clientMessage.getAppUser().setEmail(null);
        clientMessage.getAppUser().setPhone(null);

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + "user", clientMessage);
    }

    @MessageMapping("/chat.sendComment")
    public void SendComment (SimpMessageHeaderAccessor headerAccessor, @Payload Comment comment) {
        ClientComment clientComment = new ClientComment();
        ClientMessage message = new ClientMessage();
        int sessionId = getSessionId(headerAccessor);
        String content;

        comment.setContent(Html.decodeParseLines(comment.getContent()));

        if (comment.getId() == 0 || comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return ;
        }
        message.setId(comment.getId());

        content = comment.getContent().trim();
        if (content.length() >= 2000)
            content = content.substring(0, 1996) + "...";

        clientComment.setContent(content);
        clientComment.setClientMessage(message);
        clientComment.setAppUser(AppUser.duplicate((AppUser) headerAccessor.getSessionAttributes().get("appUser")));
        clientComment.setType("COMMENT");
        clientComment.setFields(((Session) headerAccessor.getSessionAttributes().get("session")).getFields());

        clientComment = clientCommentRepo.save(clientComment);

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + Admin.token, clientComment);

        clientComment.getAppUser().setFields(null);
        clientComment.getAppUser().setEmail(null);
        clientComment.getAppUser().setPhone(null);

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + "user", clientComment);
    }

    @MessageMapping("/chat.deleteItem")
    public void deleteItem (SimpMessageHeaderAccessor headerAccessor, @Payload DeleteItem item) {
        int sessionId = getSessionId(headerAccessor);
        ClientDeleteItem deletedItem = new ClientDeleteItem();

        if (!headerAccessor.getSessionAttributes().get("role").toString().equals("admin"))
            return ;
        deletedItem.setItem(item.getType());
        deletedItem.setId(item.getId());
        deletedItem.setType("DELETE");

        if (item.getType().equals("comment")) {
            clientCommentRepo.deleteById(item.getId());

            simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/common", deletedItem);
        }
        else if (item.getType().equals("message")) {
            clientMessageRepo.deleteById(item.getId());

            simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/common", deletedItem);
        }
    }

    @MessageMapping("/chat.removeUser")
    public void removeUser (SimpMessageHeaderAccessor headerAccessor) {
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

        rmRf(headerAccessor, "appUser");
        rmRf(headerAccessor, "name");
        rmRf(headerAccessor, "role");
        rmRf(headerAccessor, "email");
        rmRf(headerAccessor, "phone");

        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + "user", userEvent);
        simpMessagingTemplate.convertAndSend("/topic/" + sessionId + "/" + Admin.token, userEvent);
    }

    public static int getSessionId(SimpMessageHeaderAccessor headerAccessor) {
        Session session = (Session) headerAccessor.getSessionAttributes().get("session");

        return session.getId();
    }

    public static void rmRf(SimpMessageHeaderAccessor headerAccessor, String key) {
        try {
            headerAccessor.getSessionAttributes().remove(key);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
