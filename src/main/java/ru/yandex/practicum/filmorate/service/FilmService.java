package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Autowired
    public void userService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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

    public boolean setLikeFilm(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);

        if (film == null || user == null) {
            return false;
        }

        film.setLikeByUserId(userId);
        return true;
    }

    public Film removeLikeFilm(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);

        if (film == null) {
            return null;
        }

        if (!film.removeLikeByUserId(userId)) {
            return null;
        }

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserLikeIds().size(), f1.getUserLikeIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
