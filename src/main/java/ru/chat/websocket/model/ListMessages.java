package ru.chat.websocket.model;

public class ListMessages {
    private Iterable<ClientMessage> messages;

    public Iterable<ClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(Iterable<ClientMessage> messages) {
        this.messages = messages;
    }
}
