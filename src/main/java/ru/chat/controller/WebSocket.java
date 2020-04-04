package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.utils.Html;
import ru.chat.utils.Time;
import ru.chat.websocket.model.*;

import java.util.Iterator;

@Controller
public class WebSocket {
    @Autowired
    private SimpUserRegistry userRegistry;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @MessageMapping("/chat.listMessages")
    public void lastMessages(SimpMessageHeaderAccessor headerAccessor, @Payload Limits limits) {

        if (limits.getOffset() > 100 || limits.getLimit() <= 0 || limits.getLimit() > 100)
            return ;

        ListMessages listMessages = new ListMessages();
        Iterable<ClientMessage> messages = clientMessageRepo.findAll(limits.getOffset(), limits.getLimit());

        if (!headerAccessor.getSessionAttributes().get("role").toString().equals("admin")) {
            Iterator<ClientMessage> iterator = messages.iterator();
            ClientMessage tmp;

            while (iterator.hasNext()) {
                tmp = iterator.next();

                tmp.setEmail(null);
                tmp.setPhone(0);
            }
        }
        listMessages.setMessages(messages);

        simpMessagingTemplate.convertAndSend("/topic/" +
                headerAccessor.getSessionAttributes().get("role").toString() + "/" +
                headerAccessor.getSessionId(),
                listMessages);
    }

    @MessageMapping("/chat.addUser")
    public void addUser (SimpMessageHeaderAccessor headerAccessor) {
        UserEvent userEvent = new UserEvent();

        userEvent.setDate(Time.getNowDate());
        userEvent.setRole(headerAccessor.getSessionAttributes().get("role").toString());
        userEvent.setType("ADD");
        userEvent.setName(headerAccessor.getSessionAttributes().get("name").toString());

        simpMessagingTemplate.convertAndSend("/topic/" + "user", userEvent);
        simpMessagingTemplate.convertAndSend("/topic/" + "admin", userEvent);
    }

    @MessageMapping("/chat.sendMessage")
    public void SendMessage (SimpMessageHeaderAccessor headerAccessor, @Payload Message message) {
        ClientMessage clientMessage = new ClientMessage();
        String content;

        message.setContent(Html.decodeParseLines(message.getContent()));

        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ;
        }

        content = message.getContent().trim();
        if (content.length() >= 2000)
            content = content.substring(0, 1996) + "...";

        clientMessage.setName(headerAccessor.getSessionAttributes().get("name").toString());
        clientMessage.setContent(content);
        clientMessage.setRole(headerAccessor.getSessionAttributes().get("role").toString());
        clientMessage.setType("MESSAGE");

        try {
            clientMessage.setPhone(Long.parseLong(headerAccessor.getSessionAttributes().get("phone").toString()));
        }
        catch (NullPointerException | NumberFormatException e) {
            //clientMessage.setPhone("none");
            e.printStackTrace();
        }
        try {
            clientMessage.setEmail(headerAccessor.getSessionAttributes().get("email").toString());
        }
        catch (NullPointerException e) {
            //clientMessage.setEmail("none");
        }

        clientMessage = clientMessageRepo.save(clientMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + "admin", clientMessage);

        clientMessage.setPhone(0);
        clientMessage.setEmail(null);
        simpMessagingTemplate.convertAndSend("/topic/" + "user", clientMessage);
    }
}
