package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String login;
    private String email;
    LocalDate birthday = LocalDate.of(1990, 1, 1);
    private Set<Long> friends = new HashSet<>();

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public Set<Long> getFriends() {
        return new HashSet<>(friends);
    }

    public void addFriend(long userId) {
        friends.add(userId);
    }

    public void removeFriend(long userId) {
        friends.remove(userId);
    }
}
