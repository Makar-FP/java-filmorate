package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate = LocalDate.of(1990, 1, 1);
    private Set<Long> userLikeIds = new HashSet<>();
    private int duration;

    public Set<Long> getUserLikeIds() {
        return new HashSet<>(userLikeIds);
    }

    public void setLikeByUserId(long userId) {
        userLikeIds.add(userId);
    }

    public void removeLikeByUserId(long userId) {
        userLikeIds.remove(userId);
    }
}
