package ru.chat.model;

public class SessionData {
    private String name;
    private String key;
    private String[] fields;
    private String user;
    private String phone;
    private boolean useNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isUseNumber() {
        return useNumber;
    }

    public void setUseNumber(boolean useNumber) {
        this.useNumber = useNumber;
    }

    public static void parseFields(SessionData sessionData) {
        int len = 0;
        String[] result = null;

        if (sessionData.getFields() == null || sessionData.getFields().length == 0) {
            sessionData.setFields(result);
            return ;
        }

        for (int i = 0; i < sessionData.getFields().length; i++) {
            if (sessionData.getFields()[i].length() > 0) {
                ++len;
            }
        }
        if (len == 0) {
            sessionData.setFields(result);
            return ;
        }

        result = new String[len];
        len = 0;

        for (int i = 0; i < sessionData.getFields().length; i++) {
            if (sessionData.getFields()[i].length() > 0) {
                result[len] = sessionData.getFields()[i].length() > 49 ? sessionData.getFields()[i].substring(0, 49) : sessionData.getFields()[i];

                ++len;
            }
        }
        sessionData.setFields(result);
    }

    public static boolean validateName(String name) {
        char[] array = name.toCharArray();

        for (int i = 0; i < array.length; i++) {
            if (!Character.isDigit(array[i]) && !Character.isAlphabetic(array[i]) && array[i] != '_' && array[i] != '-') {
                return false;
            }
        }
        return true;
    }
}
