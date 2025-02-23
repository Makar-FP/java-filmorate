package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User getById(long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return user;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User updateUser(User user) {
        if (userStorage.getById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
        validateUser(user);
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userStorage.getById(userId) == null || userStorage.getById(friendId) == null) {
            throw new NotFoundException("User or Friend not found");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.getById(userId) == null || userStorage.getById(friendId) == null) {
            throw new NotFoundException("User or Friend not found");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(long userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("User with id " + userStorage.getById(userId) + " not found");
        }
        return List.copyOf(userStorage.getUserFriends(userId));
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return List.copyOf(userStorage.getCommonFriends(userId, otherId));
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login can't be empty and contain spaces");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email can't be empty and must contain @");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday can't be in the future");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}