package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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
        Film film = storage.get(id);
        if (film != null) {
            return film;
        } else {
            String errorMessage = "Film with id " + id + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
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
            log.info("Film updated: {}", entity);
            return entity;
        } else {
            String errorMessage = "Film with id " + entity.getId() + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private long getNextId() {
        return nextId++;
    }

    @Override
    public boolean exists(long filmId) {
        return storage.containsKey(filmId);
    }
}
