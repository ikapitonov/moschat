package ru.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("user.timezone", "Europe/Moscow");

        SpringApplication.run(Application.class, args);
    }
}
