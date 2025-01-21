package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final List<User> storageUser = new ArrayList<>();
    private int nextId = 0;
    private LocalDate currentDate = LocalDate.now();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * POST /users
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        try {
            validateUser(user);
            user.setId(getNextId());
            storageUser.add(user);
            return user;
        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * POST /users/
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        try {
            validateUser(user);
            for (User existingUser : storageUser) {
                if (existingUser.getId() == user.getId()) {
                    existingUser.setName(user.getName());
                    existingUser.setLogin(user.getLogin());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setBirthday(user.getBirthday());
                    log.info("User updated: {}", existingUser);
                    return existingUser;
                }
            }
            String errorMessage = "User with id " + user.getId() + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } catch (IllegalArgumentException e) {
            log.error("Error updating user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET /users
     */
    @GetMapping
    public List<User> getUsers() {
        log.info("Get users: {}", storageUser.size());
        return storageUser;
    }

    /**
     * GET users/{userId}
     */
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") int userId) {
        for (User user : storageUser) {
            if (user.getId() == userId) {
                return user;
            }
        }
        String errorMessage = "User with id " + userId + " was not found!";
        log.error(errorMessage);
        throw new IllegalArgumentException("User with id " + userId + " was not found!");
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Validation failed: Login can't be empty and contain spaces");
            throw new IllegalArgumentException("Login can't be empty and contain spaces");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Login can't be empty and must contain @");
            throw new IllegalArgumentException("Login can't be empty and must contain @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(currentDate)) {
            log.error("Birthday can't be in future");
            throw new IllegalArgumentException("Birthday can't be in future");
        }
    }

    private int getNextId() {
        return nextId++;
    }
}
