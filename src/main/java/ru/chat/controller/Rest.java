package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.auth.AuthData;
import ru.chat.model.CreateSession;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.OffsetBasedPageRequest;
import ru.chat.repositories.SessionRepo;
import ru.chat.websocket.model.ClientComment;
import ru.chat.websocket.model.ClientMessage;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import java.util.Iterator;
import java.util.List;

@RestController
public class Rest {

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private SessionRepo sessionRepo;

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
    public List<ClientMessage> listMessages(@RequestParam(value = "offset") int offset,
                                                @RequestParam(value = "limit") int limit,
                                                @RequestParam(value = "token", defaultValue = "") String token,
                                                @RequestParam(value = "sessionId") int sessionId) {
        if (limit > 100 || limit <= 0 || offset > 100)
            return null;

        boolean isAdmin = token != null && !token.isEmpty() && token.equals(Admin.token);
        List<ClientMessage> messages = clientMessageRepo.findBySessionId(sessionId, new OffsetBasedPageRequest(limit, offset));
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

    @GetMapping(value = "/create-session")
    public CreateSession createSession(@RequestParam("name") String name) {
        CreateSession session = new CreateSession();
        Session dbSession = new Session();

        if (name == null || name.isEmpty()) {
            session.setStatus(false);
            return session;
        }
        if (!sessionRepo.findByNameEquals(name).isEmpty()) {
            session.setStatus(false);
            return session;
        }
        dbSession.setName(name.length() > 50 ? name.substring(0, 50) : name);
        dbSession = sessionRepo.save(dbSession);

        session.setSession(dbSession);
        session.setStatus(true);
        return session;
    }
}
