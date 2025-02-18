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
