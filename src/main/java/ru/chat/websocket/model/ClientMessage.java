package ru.chat.websocket.model;

import org.hibernate.annotations.CreationTimestamp;
import ru.chat.utils.Time;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ClientMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(fetch=FetchType.EAGER, mappedBy="clientMessage", orphanRemoval=true)
    private List<ClientComment> comments;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private Session session;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(nullable = false, length = 2000)
    private String content;

    // думаю пока нет смысла выносить их в отдельную таблицу
    @Column(nullable = false, length = 20)
    private String role;

    @Column(length = 50)
    private String email;

    @Column(nullable = true)
    private long phone;

    @Column(nullable = false, length = 20)
    private String type;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public List<ClientComment> getComments() {
        return comments;
    }

    public void setComments(List<ClientComment> comments) {
        this.comments = comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return Time.getFormatDate(date);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
