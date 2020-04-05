package ru.chat.websocket.model;

import java.util.Iterator;

public class ListMessages {
    private Iterator<ClientMessage> messages;

    public Iterator<ClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(Iterator<ClientMessage> messages) {
        this.messages = messages;
    }
}
