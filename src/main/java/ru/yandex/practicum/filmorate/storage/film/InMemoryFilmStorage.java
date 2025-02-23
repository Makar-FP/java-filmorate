package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film create(Film entity) {
        entity.setId(getNextId());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Film getById(long id) {
        return storage.get(id);
    }

    @Override
    public List<Film> getAll() {
        log.info("Get films: {}", storage.size());
        return new ArrayList<>(storage.values());
    }

    @Override
    public Film update(Film entity) {
        if (storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
            log.info("User updated: {}", entity);
            return entity;
        }
        return null;
    }

    private long getNextId() {
        return nextId++;
    }

    @Override
    public boolean setLikeFilm(long filmId, long userId) {
        return false;
    }

    @Override
    public Film removeLikeFilm(long filmId, long userId) {
        return null;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return List.of();
    }

    @Override
    public List<Genre> getAllGenres() {
        return List.of();
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public List<Mpa> findAllMpa() {
        return List.of();
    }

    @Override
    public Mpa findMpa(int id) {
        return null;
    }
}
