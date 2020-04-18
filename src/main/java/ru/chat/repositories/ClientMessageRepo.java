package ru.chat.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.chat.websocket.model.ClientMessage;

import java.util.List;


public interface ClientMessageRepo extends CrudRepository<ClientMessage, Long> {

//    @Query(value="select * from client_message left join client_comment on client_message.id=client_comment.message_id order by client_message.id desc offset ?1 limit ?2", nativeQuery = true)
//    Iterable<ClientMessage> findAll(int offset, int limit);

  //  Iterable<ClientMessage> findAll(Pageable pageable);

    List<ClientMessage> findBySessionId(Integer idSession, Pageable pageable);
}
