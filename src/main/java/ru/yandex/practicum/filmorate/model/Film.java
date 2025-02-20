package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private int duration;
    private Mpa mpa;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    @JsonIgnore
    private Set<Long> userLikeIds = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }

    public void clearGenres() {
        genres.clear();
    }

    public Film setId(Long id) {
        this.id = id;
        return this;
    }

    public Film setName(String name) {
        this.name = name;
        return this;
    }

    public Film setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public Film setDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public Film setDescription(String description) {
        this.description = description;
        return this;
    }

    public Film setGenres(LinkedHashSet<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public Set<Long> getUserLikeIds() {
        return new HashSet<>(userLikeIds);
    }

    public void setLikeByUserId(long userId) {
        userLikeIds.add(userId);
    }

    public boolean removeLikeByUserId(long userId) {
        return userLikeIds.remove(userId);
    }
}
