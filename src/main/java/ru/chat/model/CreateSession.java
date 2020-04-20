package ru.chat.model;

import ru.chat.websocket.model.Session;

public class CreateSession {
    private boolean status;
    private String error;
    private Session session;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
