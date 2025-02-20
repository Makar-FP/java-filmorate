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
import java.util.Map;
import java.util.Collection;
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
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        if (userService.getById(user.getId()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        try {
            validateUser(user);
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        if (userService.getById(userId) == null || userService.getById(friendId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User or Friend not found"));
        }
        userService.addFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
        if (userService.getById(userId) == null || userService.getById(friendId) == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        userService.removeFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable("id") long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Collection<User> friends = userService.getUserFriends(userId);
        return ResponseEntity.ok(List.copyOf(friends));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<?> getCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
        if (userService.getById(userId) == null || userService.getById(otherId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User(s) not found"));
        }
        Collection<User> commonFriends = userService.getCommonFriends(userId, otherId);
        List<Map<String, Long>> response = commonFriends.stream()
                .map(user -> Map.of("id", user.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Validation failed: Login can't be empty and contain spaces");
            throw new IllegalArgumentException("Login can't be empty and contain spaces");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Validation failed: Email can't be empty and must contain @");
            throw new IllegalArgumentException("Email can't be empty and must contain @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(currentDate)) {
            log.error("Validation failed: Birthday can't be in the future");
            throw new IllegalArgumentException("Birthday can't be in the future");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}