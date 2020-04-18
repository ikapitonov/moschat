package ru.chat.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.chat.websocket.model.Session;

import java.util.List;

public interface SessionRepo extends CrudRepository<Session, Integer> {
    List<Session> findByNameEquals(String name);
}
