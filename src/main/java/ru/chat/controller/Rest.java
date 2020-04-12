package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.auth.AuthData;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.OffsetBasedPageRequest;
import ru.chat.websocket.model.ClientComment;
import ru.chat.websocket.model.ClientMessage;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import java.util.Iterator;

@RestController
public class Rest {

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private User user;

    @Autowired
    private Admin admin;

    @GetMapping(value = "/auth/user")
    public AuthData authUser(@RequestParam(value = "name") String name,
                             @RequestParam(value = "email", required=false) String email,
                             @RequestParam(value = "phone", required=false) String phone) {
        return user.validateHttp(name, phone, email);
    }

    @GetMapping(value = "/auth/admin")
    public AuthData authAdmin(@RequestParam(value = "login") String login,
                              @RequestParam(value = "password") String password) {
        return admin.validateHttp(login, password);
    }

    @GetMapping(value = "/listMessages")
    public Iterable<ClientMessage> listMessages(@RequestParam(value = "offset") int offset,
                             @RequestParam(value = "limit") int limit,
                             @RequestParam(value = "token", defaultValue = "") String token) {
        if (limit > 100 || limit <= 0 || offset > 100)
            return null;

        boolean isAdmin = token != null && !token.isEmpty() && token.equals(Admin.token);
        Iterable<ClientMessage> messages = clientMessageRepo.findAll(new OffsetBasedPageRequest(limit, offset));
        Iterator<ClientMessage> messageIterator = messages.iterator();
        Iterator<ClientComment> commentIterator;
        ClientMessage message;
        ClientComment comment;

        while (messageIterator.hasNext()) {
            message = messageIterator.next();

            if (!isAdmin) {
                message.setEmail(null);
                message.setPhone(0);
            }
            commentIterator = message.getComments().iterator();

            while (commentIterator.hasNext()) {
                comment = commentIterator.next();

                if (!isAdmin) {
                    comment.setEmail(null);
                    comment.setPhone(0);
                }
                comment.setClientMessage(null);
            }
        }
        return messages;
    }
}
