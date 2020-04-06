package ru.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Http {

    @GetMapping(value = "/")
    public String panel () {

        return "page";
    }

    @GetMapping(value = "/moderator")
    public String admin() {

        return "admin";
    }
}
