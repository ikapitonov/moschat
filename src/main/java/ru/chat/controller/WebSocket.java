package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import ru.chat.repositories.ClientCommentRepo;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.utils.Html;
import ru.chat.utils.Time;
import ru.chat.websocket.model.*;


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

        user.setUsername(headerAccessor.getSessionAttributes().get("name").toString());
        user.setType("WRITE");
        user.setSession(headerAccessor.getSessionId());
        simpMessagingTemplate.convertAndSend("/topic/" + "common", user);
    }

//    @MessageMapping("/chat.listMessages")
//    public void listMessages(SimpMessageHeaderAccessor headerAccessor, @Payload Limits limits) {
//        if (limits.getOffset() > 100 || limits.getLimit() <= 0 || limits.getLimit() > 100)
//            return ;
//
//        boolean isAdmin = headerAccessor.getSessionAttributes().get("role").toString().equals("admin");
//        Iterable<ClientMessage> messages = clientMessageRepo.findAll(new OffsetBasedPageRequest(limits.getLimit(), limits.getOffset()));
//        Iterator<ClientMessage> messageIterator = messages.iterator();
//        Iterator<ClientComment> commentIterator;
//        ClientMessage message;
//        ClientComment comment;
//
//        while (messageIterator.hasNext()) {
//            message = messageIterator.next();
//
//            if (!isAdmin) {
//                message.setEmail(null);
//                message.setPhone(0);
//            }
//            commentIterator = message.getComments().iterator();
//
//            while (commentIterator.hasNext()) {
//                comment = commentIterator.next();
//
//                if (!isAdmin) {
//                    comment.setEmail(null);
//                    comment.setPhone(0);
//                }
//                comment.setClientMessage(null);
//            }
//        }
//
//        simpMessagingTemplate.convertAndSend("/topic/" +
//                headerAccessor.getSessionAttributes().get("role").toString() + "/" +
//                headerAccessor.getSessionId(),
//                messages);
//    }

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

    // поскольку авторизации нет - нижепредставленный код может показаться несуразным
    // с другой стороны, его (как и тот, что выше) с появлением авторизации придется переписать, поэтому нормально
    @MessageMapping("/chat.sendComment")
    public void SendComment (SimpMessageHeaderAccessor headerAccessor, @Payload Comment comment) {
        ClientComment clientComment = new ClientComment();
        ClientMessage message = new ClientMessage();
        String content;

        comment.setContent(Html.decodeParseLines(comment.getContent()));

        if (comment.getId() == 0 || comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return ;
        }
        message.setId(comment.getId());

        content = comment.getContent().trim();
        if (content.length() >= 2000)
            content = content.substring(0, 1996) + "...";

        clientComment.setName(headerAccessor.getSessionAttributes().get("name").toString());
        clientComment.setContent(content);
        clientComment.setRole(headerAccessor.getSessionAttributes().get("role").toString());
        clientComment.setType("COMMENT");
        clientComment.setClientMessage(message);

        try {
            clientComment.setPhone(Long.parseLong(headerAccessor.getSessionAttributes().get("phone").toString()));
        }
        catch (NullPointerException | NumberFormatException e) {
            //clientMessage.setPhone("none");
        }
        try {
            clientComment.setEmail(headerAccessor.getSessionAttributes().get("email").toString());
        }
        catch (NullPointerException e) {
            //clientMessage.setEmail("none");
        }

        clientComment = clientCommentRepo.save(clientComment);
        simpMessagingTemplate.convertAndSend("/topic/" + "admin", clientComment);

        clientComment.setPhone(0);
        clientComment.setEmail(null);
        simpMessagingTemplate.convertAndSend("/topic/" + "user", clientComment);
    }
}
