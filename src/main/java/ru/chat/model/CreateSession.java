package ru.chat.model;

import ru.chat.websocket.model.Session;

public class CreateSession {
    private boolean status;
    private Session session;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
