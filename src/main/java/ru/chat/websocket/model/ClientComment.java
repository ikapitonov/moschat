package ru.chat.websocket.model;

import org.hibernate.annotations.CreationTimestamp;
import ru.chat.utils.Time;

import javax.persistence.*;
import java.time.LocalDateTime;

// пока нет авторизации придется записывать в обе таблицы одно и то же
@Entity
@Table(name = "client_comment")
public class ClientComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id", nullable=false)
    private ClientMessage clientMessage;

//   // @ManyToOne
//    @Column(name = "message_id")
//    private long clientMessage;

    @CreationTimestamp
    private LocalDateTime date;

    @Column(nullable = false, length = 2000)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Column(nullable = false, length = 20)
    private String type;

    @Transient
    private String fields;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClientMessage getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
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

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
