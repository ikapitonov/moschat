package ru.chat.auth;

import java.util.LinkedList;
import java.util.List;

public class Item {
    private String session;
    private String user;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static List generateItems(String[] fromSession, String[] fromUser) {
        List<Item> items = new LinkedList();
        Item item;

        if (fromSession == null || fromSession.length == 0) {
            return null;
        }

        for (int i = 0;  i < fromSession.length; i++) {
            item = new Item();

            item.setSession(fromSession[i]);
            item.setUser(fromUser == null || i >= fromUser.length ? null : fromUser[i]);

            items.add(item);
        }
        return items;
    }
}
