package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public User create(User entity) {
        entity.setId(getNextId());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public User getById(long id) {
        return storage.get(id);
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
        }
        return null;
    }

    private long getNextId() {
        return nextId++;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        return List.of();
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return List.of();
    }
}
