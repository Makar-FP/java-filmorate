package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final FilmStorage filmStorage;

    public MpaService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public Mpa findMpa(int id) {
        validateMpa(id);
        return filmStorage.findMpa(id);
    }

    private void validateMpa(int mpaId) {
        Set<Integer> validMpaIds = findAllMpa().stream()
                .map(Mpa::getId)
                .collect(Collectors.toSet());

        if (!validMpaIds.contains(mpaId)) {
            throw new NotFoundException("MPA with id " + mpaId + " was not found");
        }
    }
}
