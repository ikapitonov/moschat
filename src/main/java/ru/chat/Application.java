package ru.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("user.timezone", "Europe/Moscow");
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));

        SpringApplication.run(Application.class, args);
    }
}
