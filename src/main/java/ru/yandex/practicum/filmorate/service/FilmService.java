package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public void createFilm(Film film) {
        filmStorage.create(film);
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void updateFilm(Film film) {
        filmStorage.update(film);
    }

    public Film setLikeFilm(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);
        film.setLikeByUserId(userId);
        return film;
    }

    public Film removeLikeFilm(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);
        film.removeLikeByUserId(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
