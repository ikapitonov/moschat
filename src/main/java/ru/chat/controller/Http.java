package ru.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.chat.auth.AuthData;
import ru.chat.auth.Item;
import ru.chat.model.ColorItem;
import ru.chat.repositories.ClientMessageRepo;
import ru.chat.repositories.SessionRepo;
import ru.chat.utils.Codec;
import ru.chat.utils.Color;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import javax.servlet.http.HttpServletRequest;
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
        model.addAttribute("phone", auth.getPhone() == null ? null : auth.getPhone());

        List<Session> list = sessionRepo.findByNameEquals(sessionName);

        if (list.isEmpty()) {
            return "redirect:/" + COMMON_ROOM;
        }
        boolean usePhone = list.get(0).isUsePhone();

        model.addAttribute("sessionId", list.get(0).getId());
        model.addAttribute("username", list.get(0).getUserName());
        model.addAttribute("phonename", list.get(0).getPhoneName());
        model.addAttribute("is_phone", usePhone);
        model.addAttribute("fields", Item.generateItems(Codec.unmergeStrings(list.get(0).getFields()), fields));

        if (list.get(0).getColors() == null || list.get(0).getColors().isEmpty()) {
            model.addAttribute("colors", Color.getDefaults());
        }
        else {
            model.addAttribute("colors", Color.deConvert(list.get(0).getColors()));
        }

        return "user";
    }

    @GetMapping(value = "/admin/create-session")
    public String createSession() {

        return "create";
    }

    @GetMapping(value = "/admin/colors")
    public String colors() {

        return "colors";
    }

    @PostMapping(value = "/admin/colors")
    public String postColors(@RequestParam(value = "name") String name, @RequestParam(value = "key") String key, HttpServletRequest request) {
        if (name == null || name.isEmpty() || key == null || key.isEmpty()) {
            return "redirect:/admin/colors?error=empty params";
        }
        if (!key.equals(Admin.key)) {
            return "redirect:/admin/colors?error=invalid key";
        }
        List<Session> list = sessionRepo.findByNameEquals(name);

        if (list.isEmpty()) {
            return "redirect:/admin/colors?error=room not found";
        }
        request.getSession().setAttribute("auth", true);

        return "redirect:/admin/change-colors?name=" + name;
    }

    @GetMapping(value = "/admin/change-colors")
    public String changeColors(@RequestParam(value = "name") String name, HttpServletRequest request, Model model) {
        if (!isAuth(request)) {
            return "redirect:/admin/colors?error=invalid auth";
        }
        List<Session> list = sessionRepo.findByNameEquals(name);
        Session session;

        if (list.isEmpty()) {
            return "redirect:/admin/colors?error=room not found";
        }
        session = list.get(0);

        model.addAttribute("fields", Color.generate(session.getColors()));
        model.addAttribute("id", session.getId());
        model.addAttribute("name", name);

        return "change_colors";
    }

    @PostMapping(value = "/admin/change-colors")
    public String postChangeColors(@RequestParam(value = "hex") String[] hex, @RequestParam(value = "id") int id, @RequestParam(value = "name") String name, HttpServletRequest request) {
        if (!isAuth(request)) {
            return "redirect:/admin/change-colors?error=invalid auth&name=" + name;
        }
        if (!Color.validate(hex)) {
            return "redirect:/admin/change-colors?error=invalid params&name=" + name;
        }
        Session session;

        try {
            session = sessionRepo.findById(id).get();
        }
        catch (Exception e) {
            return "redirect:/admin/change-colors?error=room not found&name=" + name;
        }
        session.setColors(Color.convert(hex));
        sessionRepo.save(session);

        return "redirect:/admin/change-colors?name=" + session.getName();
    }

    @GetMapping(value = "/admin/test")
    public String adminTest(@RequestParam(value = "hex") String[] hex, @RequestParam(value = "name") String name, HttpServletRequest request, Model model) {
        for (int i = 0; hex != null && i < hex.length; i++) {
            hex[i] = "#" + hex[i];
        }

        if (!isAuth(request)) {
            return "redirect:/admin/change-colors?error=invalid auth&name=" + name;
        }
        for (String s: hex) {
            System.out.println(s);
        }
        if (!Color.validate(hex)) {
            return "redirect:/admin/change-colors?error=invalid params&name=" + name;
        }
        model.addAttribute("colors", hex);

        return "test_watch";
    }

    private boolean isAuth(HttpServletRequest request) {
        try {
            Boolean auth = (Boolean) request.getSession().getAttribute("auth");

            return auth.equals(Boolean.TRUE);
        }
        catch (Exception e) {
            return false;
        }
    }
}
