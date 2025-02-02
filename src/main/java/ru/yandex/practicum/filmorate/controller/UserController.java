package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private LocalDate currentDate = LocalDate.now();

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            validateUser(user);
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            validateUser(user);
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable("userId") long userId, @PathVariable("friendId") long friendId) {
        User user = userService.addFriend(userId, friendId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable("userId") long userId, @PathVariable("friendId") long friendId) {
        User user = userService.removeFriend(userId, friendId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/friends/")
    public Set<Long> getFriends(@PathVariable("userId") long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/common/{otherId}")
    public Set<Long> getCommonFriends(@PathVariable("userId") long userId, @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(userId, otherId);
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
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}