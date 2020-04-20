package ru.chat.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.chat.repositories.AppUserRepo;
import ru.chat.websocket.model.AppUser;

import java.util.NoSuchElementException;
import java.util.Random;

@Component
public class Token {

    @Autowired
    private AppUserRepo appUserRepo;

    public static final String SOURCES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public static final int LEN = 10;

    public static String generate() {
        Random random = new Random();
        char[] text = new char[10];

        for (int i = 0; i < 10; i++) {
            text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
        }
        return new String(text);
    }

    public boolean validateToken(long id, String token, int sessionId) {
        AppUser appUser;

        if (id == 0 || token == null || token.isEmpty() || token.length() != LEN) {
            return false;
        }
        try {
            appUser = appUserRepo.findById(id).get();
        }
        catch (NoSuchElementException e) {
            return false;
        }

        try {
            return appUser.getToken().equals(token) && appUser.getSessionId() == sessionId;
        }
        catch (Exception e) {
            return false;
        }
    }
}
