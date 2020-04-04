package ru.chat.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.chat.websocket.model.ClientMessage;

import java.util.List;

public interface ClientMessageRepo extends CrudRepository<ClientMessage, Long> {

    @Query(value="select * from client_message order by id desc offset ?1 limit ?2", nativeQuery = true)
    List<ClientMessage> findAll(int offset, int limit);
}
