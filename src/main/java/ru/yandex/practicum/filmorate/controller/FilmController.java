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
    private final FilmService filmService;;
    private final LocalDate thresholdDate = LocalDate.of(1895, 12, 28);

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            filmService.createFilm(film);
            return ResponseEntity.status(HttpStatus.CREATED).body(film);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(film);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            filmService.updateFilm(film);
            return ResponseEntity.ok(film);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable("id") long filmId) {
        return ResponseEntity.ok(filmService.getById(filmId));
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new IllegalArgumentException("Film name can't be empty");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(thresholdDate)) {
            throw new IllegalArgumentException("Release date can't be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("The duration must be a positive number.");
        }
        if (film.getDescription().length() > 200) {
            throw new IllegalArgumentException("String length can't exceed 200 characters.");
        }
    }
}