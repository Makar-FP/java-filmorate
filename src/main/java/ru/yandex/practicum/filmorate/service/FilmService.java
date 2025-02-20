package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
        return filmStorage.setLikeFilm(filmId, userId);
    }

    public Film removeLikeFilm(long filmId, long userId) {
        return filmStorage.removeLikeFilm(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public Mpa findMpa(int id) {
        return filmStorage.findMpa(id);
    }
}
