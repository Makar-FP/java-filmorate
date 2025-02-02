package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * POST /films
     */
    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        filmService.createFilm(film);
        return film;
    }

    /**
     * PUT /films/
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        filmService.updateFilm(film);
        return film;
    }

    /**
     * GET /films
     */
    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAll();
    }

    /**
     * GET films/{filmId}
     */
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") long filmId) {
        return filmService.getById(filmId);
    }

    /**
     * PUT films/{filmId}/like/{userId}
     */
    @PutMapping("/{filmId}/like/{userId}")
    public Film setLikeFilm (@PathVariable("filmId") long filmId, @PathVariable("userId") long userId) {
        return filmService.setLikeFilm(filmId, userId);
    }

    /**
     * DELETE films/{filmId}/like/{userId}
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLikeFilm (@PathVariable("filmId") long filmId, @PathVariable("userId") long userId) {
        return filmService.removeLikeFilm(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
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
}
