package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            validateUser(user);
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        try {
            User user = userService.addFriend(userId, friendId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userService.getById(userId));
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        User user = userService.removeFriend(userId, friendId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/friends")
    public List<Map<String, Long>> getFriends(@PathVariable("id") long userId) {
        List<Map<String, Long>> friends = userService.getFriends(userId)
                .stream()
                .map(friendId -> Map.of("id", friendId))
                .collect(Collectors.toList());
        return friends;
    }

    @GetMapping("/{id}/common/{otherId}")
    public ResponseEntity<List<Map<String, Long>>> getCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
        Set<Long> commonFriends = userService.getCommonFriends(userId, otherId);
        if (commonFriends.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<Map<String, Long>> response = commonFriends.stream()
                .map(friendId -> Map.of("id", friendId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
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
        log.error("Validation error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}