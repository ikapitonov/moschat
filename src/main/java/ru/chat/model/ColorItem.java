package ru.chat.model;

public class ColorItem {
    private String name;
    private String hex;

    public ColorItem(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public String getHex() {
        return hex;
    }
}
