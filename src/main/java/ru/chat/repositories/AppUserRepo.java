package ru.chat.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.chat.websocket.model.AppUser;

public interface AppUserRepo extends CrudRepository<AppUser, Long> {
}
