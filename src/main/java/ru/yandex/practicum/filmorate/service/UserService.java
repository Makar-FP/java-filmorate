package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createUser(User user) {
        userStorage.create(user);
    }

    public User getById(long id) {
        return userStorage.getById(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void updateUser(User user) {
        userStorage.update(user);
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);

        if (user == null || userFriend == null) {
            throw new IllegalArgumentException("User not found");
        }

        userFriend.addFriend(userId);
        user.addFriend(friendId);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);

        if (user == null || userFriend == null) {
            return null;
        }

        userFriend.removeFriend(userId);
        user.removeFriend(friendId);
        return user;
    }


    public List<Map<String, Long>> getFriends(long userId) {
        User user = userStorage.getById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user.getFriends() != null
                ? user.getFriends().stream()
                .map(friendId -> Map.of("id", friendId))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public Set<Long> getCommonFriends(long userId, long otherId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherId);

        if (user == null || otherUser == null) {
            return Collections.emptySet();
        }

        Set<Long> common = new HashSet<>(user.getFriends());
        common.retainAll(otherUser.getFriends());

        return common;
    }
}