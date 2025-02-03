package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public User create(User entity) {
        entity.setId(getNextId()); // Установка нового ID
        storage.put(entity.getId(), entity); // Сохранение в HashMap
        return entity;
    }

    @Override
    public User getById(long id) {
        User user = storage.get(id); // Получение пользователя по ID
        if (user != null) {
            return user;
        } else {
            String errorMessage = "User with id " + id + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public List<User> getAll() {
        log.info("Get users: {}", storage.size());
        return new ArrayList<>(storage.values());
    }

    @Override
    public User update(User entity) {
        if (storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
            log.info("User updated: {}", entity);
            return entity;
        } else {
            String errorMessage = "User with id " + entity.getId() + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private long getNextId() {
        return nextId++;
    }

    @Override
    public boolean exists(long userId) {
        return storage.containsKey(userId);
    }
}
