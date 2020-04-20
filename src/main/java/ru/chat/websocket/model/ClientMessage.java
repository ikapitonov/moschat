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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private Session session;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(nullable = false, length = 2000)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Column(nullable = false, length = 20)
    private String type;

    public long getId() {
        return id;
    }

    public List<ClientComment> getComments() {
        return comments;
    }

    public void setComments(List<ClientComment> comments) {
        this.comments = comments;
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

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
