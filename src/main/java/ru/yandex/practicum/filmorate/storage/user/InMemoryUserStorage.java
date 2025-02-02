package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final List<User> storage = new ArrayList<>();
    private long nextId = 1;


    @Override
    public User create(User entinty) {
        entinty.setId(getNextId());
        storage.add(entinty);
        return entinty;
    }

    @Override
    public User getById(long id) {
        for (User entity : storage) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        String errorMessage = "Film with id " + id + " was not found!";
        log.error(errorMessage);
        throw new IllegalArgumentException("Film with id " + id + " was not found!");
    }

    @Override
    public List<User> getAll() {
        log.info("Get films: {}", storage.size());
        return storage;
    }

    @Override
    public User update(User entity) {
        try {
            for (User existingUser : storage) {
                if (existingUser.getId() == entity.getId()) {
                    existingUser.setName(entity.getName());
                    existingUser.setLogin(entity.getLogin());
                    existingUser.setEmail(entity.getEmail());
                    existingUser.setBirthday(entity.getBirthday());
                    log.info("User updated: {}", existingUser);
                    return existingUser;
                }
            }
            String errorMessage = "User with id " + entity.getId() + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } catch (IllegalArgumentException e) {
            log.error("Error updating user: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        return nextId++;
    }
}
