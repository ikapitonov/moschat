package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.chat.auth.AuthData;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.SessionRepo;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.User;

import java.util.Iterator;
import java.util.List;

@Controller
public class Http {

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private User user;

    @GetMapping(value = "/{sessionName}/chat")
    public String panel (@PathVariable String sessionName, Model model,
                         @RequestParam(value = "username", required=false) String name,
                         @RequestParam(value = "email", required=false) String email,
                         @RequestParam(value = "phone", required=false) String phone) {
        if (sessionName == null || sessionName.isEmpty()) {
            return "error";
        }
        AuthData auth = user.validateHttp(name, phone, email);

        model.addAttribute("auth", auth.isStatus());
        model.addAttribute("name", auth.getName());
        model.addAttribute("email", auth.getEmail());
        model.addAttribute("phone", auth.getPhone() == 0 ? null : auth.getPhone());

        List<Session> list = sessionRepo.findByNameEquals(sessionName);

        if (list.isEmpty()) {
            return "error";
        }
        model.addAttribute("sessionId", list.get(0).getId());

        return "user";
    }
}
