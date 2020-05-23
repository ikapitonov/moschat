package ru.chat.websocket.permissions;

import org.springframework.stereotype.Component;
import ru.chat.auth.AuthData;
import ru.chat.utils.Html;

import javax.mail.internet.InternetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class User {
    private static final int nameLength = 50;
    private static final String regex = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";

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

    public AuthData validateOnlyName(String name) {
        AuthData authData = new AuthData();

        authData.setRole("user");
        if (!validateName(name)) {
            authData.setStatus(false);
            return authData;
        }
        authData.setName(Html.fullDecode(name));
        authData.setStatus(true);
        return authData;
    }

    public boolean validateName(String name) {
        return name != null && !name.isEmpty() && name.length() < nameLength;
    }

    public boolean validatePhone(String phone) {
        return phone == null || phone.isEmpty() ? false : Pattern.matches(regex, phone);
    }

    public String getPhone(String phone) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);

        return matcher.find() ? phone.substring(matcher.start(), matcher.end()) : null;
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
