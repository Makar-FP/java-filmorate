package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final FilmStorage filmStorage;

    public GenreService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        validateGenre(id);
        return filmStorage.getGenreById(id);
    }

    private void validateGenre(int genreId) {
        Set<Integer> validGenreIds = getAllGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!validGenreIds.contains(genreId)) {
            throw new NotFoundException("Genre with id " + genreId + " was not found");
        }
    }
}
