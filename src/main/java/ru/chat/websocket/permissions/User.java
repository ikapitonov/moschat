package ru.chat.websocket.permissions;

import org.springframework.stereotype.Component;
import ru.chat.auth.AuthData;
import ru.chat.utils.Html;

import javax.mail.internet.InternetAddress;

@Component
public class User {
    private static final int nameLength = 50;

    // здесь можно проводить валидацию
    public AuthData validateHttp(String name, String phone, String email) {
        AuthData authData = new AuthData();
        boolean flag = false;

        authData.setRole("user");
        if (!validateName(name)) {
            authData.setStatus(false);
            return authData;
        }
        if (validatePhone(phone)) {
            flag = true;
            authData.setPhone(getPhone(phone));
        }
        if (validateEmail(email)) {
            flag = true;
            authData.setEmail(email);
        }
        authData.setStatus(flag);
        authData.setName(Html.fullDecode(name));
        return authData;
    }

    public boolean validateName(String name) {
        return name != null && !name.isEmpty() && name.length() < nameLength;
    }

    public boolean validatePhone(String phone) {
        String result;

        if (phone == null || phone.isEmpty())
            return false;

        result = phone.replaceAll("[\\D]","");
        return result.length() >= 9 && result.length() <= 11;
    }

    public long getPhone(String phone) {
        return Long.parseLong(phone.replaceAll("[\\D]",""));
    }

    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty() || email.length() > 50)
            return false;

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
