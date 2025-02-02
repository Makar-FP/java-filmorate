package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> storage = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Film create(Film entity) {
        entity.setId(getNextId());
        storage.add(entity);
        log.info("Film created: {}", entity);
        return entity;
    }

    @Override
    public Film getById(long id) {
        for (Film entity : storage) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        String errorMessage = "Film with id " + id + " was not found!";
        log.error(errorMessage);
        throw new IllegalArgumentException("Film with id " + id + " was not found!");
    }

    @Override
    public List<Film> getAll() {
        log.info("Get films: {}", storage.size());
        return storage;
    }

    @Override
    public Film update(Film entity) {
        try {
            for (Film existingFilm : storage) {
                if (existingFilm.getId() == entity.getId()) {
                    existingFilm.setName(entity.getName());
                    existingFilm.setDescription(entity.getDescription());
                    existingFilm.setReleaseDate(entity.getReleaseDate());
                    existingFilm.setDuration(entity.getDuration());
                    log.info("Film updated: {}", existingFilm);
                    return existingFilm;
                }
            }
            String errorMessage = "Film with id " + entity.getId() + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } catch (IllegalArgumentException e) {
            log.error("Error updating film: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        return nextId++;
    }
}
