package ru.chat.websocket.permissions;

import org.springframework.stereotype.Component;

@Component
public class Admin {
    public static final String login = "admin";
    public static final String password = "pass";
    public static final String name = "admin";

    // здесь можно проводить валидацию
    public boolean isAllowed(String login, String password) {
        if (login == null || login.isEmpty() || password == null || password.isEmpty())
            return false;

        return login.equals(Admin.login) && password.equals(Admin.password);
    }
}
