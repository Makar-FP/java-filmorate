package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LocalDate thresholdDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film getById(long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Film with ID " + id + " not found");
        }
        return film;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (filmStorage.getById(film.getId()) == null) {
            throw new NotFoundException("Film with ID " + film.getId() + " not found");
        }
        return filmStorage.update(film);
    }

    public void setLikeFilm(long filmId, long userId) {
        if (!filmStorage.setLikeFilm(filmId, userId)) {
            throw new NotFoundException("Film or User not found");
        }
    }

    public Film removeLikeFilm(long filmId, long userId) {
        Film film = filmStorage.removeLikeFilm(filmId, userId);
        if (film == null) {
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            throw new ValidationException("Film name can't be empty");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(thresholdDate)) {
            throw new ValidationException("Release date can't be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("The duration must be a positive number.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("String length can't exceed 200 characters.");
        }

        Set<Integer> validMpaIds = filmStorage.findAllMpa().stream()
                .map(Mpa::getId)
                .collect(Collectors.toSet());

        if (film.getMpa() == null || !validMpaIds.contains(film.getMpa().getId())) {
            throw new NotFoundException("Invalid or missing MPA rating");
        }
        Set<Integer> validGenreIds = filmStorage.getAllGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (film.getGenres() != null && !validGenreIds.containsAll(film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))) {
            throw new NotFoundException("Invalid or missing genre");
        }
    }
}