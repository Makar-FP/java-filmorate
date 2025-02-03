package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {
    private final FilmService filmService;
    private final LocalDate thresholdDate = LocalDate.of(1895, 12, 28);

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            filmService.createFilm(film);
            return ResponseEntity.status(HttpStatus.CREATED).body(film);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(film);
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            filmService.updateFilm(film);
            if (filmService.getById(film.getId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
            }
            return ResponseEntity.ok(film);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") long filmId) {
        Film film = filmService.getById(filmId);
        if (film == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(film);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> setLikeFilm(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        Film film = filmService.setLikeFilm(filmId, userId);
        return ResponseEntity.ok(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLikeFilm(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.removeLikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilms(count);
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        log.error("Validation error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}