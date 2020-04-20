package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.chat.auth.AdminData;
import ru.chat.auth.AuthData;
import ru.chat.auth.Token;
import ru.chat.auth.UserData;
import ru.chat.model.CreateSession;
import ru.chat.model.SessionData;
import ru.chat.repositories.AppUserRepo;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.OffsetBasedPageRequest;
import ru.chat.repositories.SessionRepo;
import ru.chat.utils.Codec;
import ru.chat.websocket.model.AppUser;
import ru.chat.websocket.model.ClientComment;
import ru.chat.websocket.model.ClientMessage;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import java.util.Iterator;
import java.util.List;

@RestController
public class Rest {
    public final static long AdminId = 1;

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private Token tokenService;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private User user;

    @Autowired
    private Admin admin;

    @PostMapping(value = "/auth/user")
    public AuthData authUser(@RequestBody UserData userData) {
        AuthData authData;

        if (userData.getId() < 0 || userData.getId() == AdminId) {
            authData = new AuthData();
            authData.setStatus(false);
            return authData;
        }

        authData = user.validateHttp(userData.getName(), userData.getPhone(), userData.getEmail());
        AppUser appUser = new AppUser();

        if (authData.isStatus()) {
            appUser.setEmail(authData.getEmail());
            appUser.setPhone(authData.getPhone());
            appUser.setSessionId(userData.getSessionId());
            appUser.setFields(userData.getFields() == null ? null : Codec.mergeStrings(Codec.generate(userData.getFields())));

            if (tokenService.validateToken(userData.getId(), userData.getToken(), userData.getSessionId())) {
                appUser.setId(userData.getId());
                appUser.setToken(userData.getToken());
            }
            else {
                appUser.setToken(Token.generate());
            }

            appUser.setRole("user");
            appUser.setName(authData.getName());
            authData.setUser(appUserRepo.save(appUser));
        }

        return authData;
    }

    @PostMapping(value = "/auth/admin")
    public AuthData authAdmin(@RequestBody AdminData adminData) {

        AuthData authData = admin.validateHttp(adminData.getLogin(), adminData.getPassword());

        if (authData.isStatus()) {
            authData.setUser(appUserRepo.findById(AdminId).get());
        }
        return authData;
    }

    @GetMapping(value = "/chat/listMessages")
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
                message.getAppUser().setFields(null);
                message.getAppUser().setEmail(null);
                message.getAppUser().setPhone(0);
            }

            commentIterator = message.getComments().iterator();

            while (commentIterator.hasNext()) {
                comment = commentIterator.next();

                if (!isAdmin) {
                    comment.getAppUser().setFields(null);
                    comment.getAppUser().setEmail(null);
                    comment.getAppUser().setPhone(0);
                }
                comment.setClientMessage(null);
            }
        }
        return messages;
    }

    @PostMapping(value = "/admin/create-session")
    public CreateSession createSession(@RequestBody SessionData sessionData) {
        CreateSession session = new CreateSession();
        Session dbSession = new Session();

        SessionData.parseFields(sessionData);

        if (sessionData.getFields() != null && sessionData.getFields().length > 5) {
            session.setStatus(false);
            session.setError("Ошибка дополнительных полей - больше 5ти");
            return session;
        }
        if (sessionData.getName() == null || sessionData.getName().isEmpty() || !SessionData.validateName(sessionData.getName())) {
            session.setStatus(false);
            session.setError("Ошибка в имени");
            return session;
        }
        if (!sessionData.getKey().equals(Admin.key)) {
            session.setStatus(false);
            session.setError("Неверный ключ");
            return session;
        }
        if (!sessionRepo.findByNameEquals(sessionData.getName()).isEmpty()) {
            session.setStatus(false);
            session.setError("Уже существует сессия с таким именем");
            return session;
        }

        dbSession.setFields(Codec.mergeStrings(sessionData.getFields()));
        dbSession.setName(sessionData.getName().length() > 50 ? sessionData.getName().substring(0, 50) : sessionData.getName());
        dbSession = sessionRepo.save(dbSession);

        session.setSession(dbSession);
        session.setStatus(true);
        return session;
    }
}
