package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/films")
@RestController
public class FilmController {
    private final List<Film> storageFilm = new ArrayList<>();
    private LocalDate thresholdDate = LocalDate.of(1895, 12, 28);
    private int nextId = 0;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    /**
     * POST /films
     */
    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(getNextId());
            storageFilm.add(film);
            log.info("Film created: {}", film);
            return film;
        } catch (IllegalArgumentException e) {
            log.error("Error creating film: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * PUT /films/
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            for (Film existingFilm : storageFilm) {
                if ((existingFilm.getId() == film.getId())) {
                    existingFilm.setName(film.getName());
                    existingFilm.setDescription(film.getDescription());
                    existingFilm.setReleaseDate(film.getReleaseDate());
                    existingFilm.setDuration(film.getDuration());
                    log.info("Film updated: {}", existingFilm);
                    return existingFilm;
                }
            }
            String errorMessage = "Film with id " + filmId + " was not found!";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } catch (IllegalArgumentException e) {
            log.error("Error updating film: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET /films
     */
    @GetMapping
    public List<Film> getFilms() {
        log.info("Get films: {}", storageFilm.size());
        return storageFilm;
    }

    /**
     * GET films/{filmId}
     */
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") int filmId) {
        for (Film film : storageFilm) {
            if (film.getId() == filmId) {
                return film;
            }
        }
        String errorMessage = "Film with id " + filmId + " was not found!";
        log.error(errorMessage);
        throw new IllegalArgumentException("Film with id " + filmId + " was not found!");
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Validation failed: Film name can't be empty");
            throw new IllegalArgumentException("Film name can't be empty");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(thresholdDate)) {
            log.error("Validation failed: Release date can't be earlier than December 28, 1895.");
            throw new IllegalArgumentException("Release date can't be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            log.error("Validation failed: The duration must be a positive number.");
            throw new IllegalArgumentException("The duration must be a positive number.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Validation failed: String length can't exceed 200 characters.");
            throw new IllegalArgumentException("String length can't exceed 200 characters.");
        }
    }

    private int getNextId() {
        return nextId++;
    }
}
