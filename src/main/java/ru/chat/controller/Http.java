package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.chat.auth.AuthData;
import ru.chat.auth.Item;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.SessionRepo;
import ru.chat.utils.Codec;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.User;

import java.util.List;

@Controller
public class Http {
    public final static String COMMON_ROOM = "common";

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private ClientMessageRepo clientMessageRepo;

    @Autowired
    private User user;

    @GetMapping(value = "/")
    public String init () {
        return "redirect:/" + COMMON_ROOM;
    }

    @GetMapping(value = "/{sessionName}")
    public String panel (@PathVariable String sessionName, Model model,
                         @RequestParam(value = "username", required=false) String name,
                         @RequestParam(value = "email", required=false) String email,
                         @RequestParam(value = "phone", required=false) String phone,
                         @RequestParam(value = "fields", required=false) String[] fields) {
        if (sessionName == null || sessionName.isEmpty()) {
            return "redirect:/" + COMMON_ROOM;
        }
        AuthData auth = user.validateHttp(name, phone, email);

        model.addAttribute("auth", auth.isStatus());
        model.addAttribute("name", auth.getName());
     //   model.addAttribute("email", auth.getEmail());
        model.addAttribute("phone", auth.getPhone() == 0 ? null : auth.getPhone());

        List<Session> list = sessionRepo.findByNameEquals(sessionName);

        if (list.isEmpty()) {
            return "redirect:/" + COMMON_ROOM;
        }
        model.addAttribute("sessionId", list.get(0).getId());
        model.addAttribute("username", list.get(0).getUserName());
        model.addAttribute("phonename", list.get(0).getPhoneName());
        model.addAttribute("fields", Item.generateItems(Codec.unmergeStrings(list.get(0).getFields()), fields));

        return "user";
    }

    @GetMapping(value = "/admin/create-session")
    public String createSession() {

        return "create";
    }
}
