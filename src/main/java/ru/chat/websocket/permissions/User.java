package ru.chat.websocket.permissions;

import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import java.util.Map;

@Component
public class User {
    private static final int nameLength = 50;

    // здесь можно проводить валидацию
    public boolean isAllowed() {
        return true;
    }

    public boolean validateName(String name) {
        return name != null && !name.isEmpty() && name.length() < nameLength;
    }

    public boolean validatePhone(Map attributes, String phone) {
        String result;
        boolean valid;

        if (phone == null || phone.isEmpty())
            return false;

        result = phone.replaceAll("[\\D]","");
        valid = result.length() >= 9 && result.length() <= 11;
        if (valid)
            attributes.put("phone", result);
        return valid;
    }

    public boolean validateEmail(Map attributes, String email) {
        if (email == null || email.isEmpty() || email.length() > 50)
            return false;

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            attributes.put("email", email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
