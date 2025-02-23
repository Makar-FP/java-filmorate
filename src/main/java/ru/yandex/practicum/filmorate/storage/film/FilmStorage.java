package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {

    public boolean setLikeFilm(long filmId, long userId);

    public Film removeLikeFilm(long filmId, long userId);

    public List<Film> getPopularFilms(int count);

    public List<Genre> getAllGenres();

    public Genre getGenreById(int id);

    public List<Mpa> findAllMpa();

    public Mpa findMpa(int id);
}
