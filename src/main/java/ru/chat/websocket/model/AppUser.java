package ru.chat.websocket.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int sessionId;

    // Максимальная длина fields - 5 (5 * 50 + 5 * 10)
    @Column(length = 301)
    private String fields;

    @Column(length = 50)
    private String name;

    @Column(length = 12)
    private String token;

    @Column(length = 50)
    private String email;

    @Column(length = 30)
    private String phone;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(nullable = false, length = 10)
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public static AppUser duplicate(AppUser user) {
        AppUser appUser = new AppUser();

        appUser.setName(user.getName());
        appUser.setRole(user.getRole());
        appUser.setEmail(user.getEmail());
        appUser.setPhone(user.getPhone());
        appUser.setDate(user.getDate());
        appUser.setId(user.getId());
        appUser.setSessionId(user.getSessionId());
        appUser.setFields(user.getFields());

        return appUser;
    }
}
