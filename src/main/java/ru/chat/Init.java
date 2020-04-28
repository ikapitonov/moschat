package ru.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.chat.controller.Http;
import ru.chat.controller.Rest;
import ru.chat.repositories.AppUserRepo;
import ru.chat.repositories.SessionRepo;
import ru.chat.websocket.model.AppUser;
import ru.chat.websocket.model.Session;

import java.util.NoSuchElementException;

@Component
public class Init {

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        try {
            Session session = sessionRepo.findById(1).get();
        }
        catch (NoSuchElementException e) {
            Session session = new Session();

            session.setName(Http.COMMON_ROOM);
            session.setUserName("Имя");
            session.setPhoneName("Номер телефона");
            sessionRepo.save(session);
        }
        try {
            AppUser appUser = appUserRepo.findById(Rest.AdminId).get();
        }
        catch (NoSuchElementException e) {
            AppUser appUser = new AppUser();

            appUser.setRole("admin");
            appUser.setName("Администратор");
            appUser.setPhone("");
            appUserRepo.save(appUser);
        }
    }
}
