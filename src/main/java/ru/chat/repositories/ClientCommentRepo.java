package ru.chat.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.chat.websocket.model.ClientComment;

public interface ClientCommentRepo extends CrudRepository<ClientComment, Long> {

}
