package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * POST /users
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        userService.createUser(user);
        return user;
    }

    /**
     * POST /users/
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        userService.updateUser(user);
        return user;
    }

    /**
     * GET /users
     */
    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    /**
     * GET users/{userId}
     */
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") long userId) {
        return userService.getById(userId);
    }

    /**
     * PUT users/{userId}/friends/{friendId}
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable("userId") long userId,@PathVariable("FriendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    /**
     * DELETE users/{userId}/friends/{friendId}
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable("userId") long userId,@PathVariable("FriendId") long friendId) {
        return userService.removeFriend(userId, friendId);
    }

    /**
     * GET users/{userId}/friends
     */
    @GetMapping("/{userId}/friends/")
    public Set<Long> getFriends(@PathVariable("userId") long userId) {
        return userService.getFriends(userId);
    }

    /**
     * GET users/{userId}/common/{otherId}
     */
    @GetMapping("/{userId}/common/{otherId}")
    public Set<Long> getFriends(@PathVariable("userId") long userId,@PathVariable("otherId") long otherId) {
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
}
