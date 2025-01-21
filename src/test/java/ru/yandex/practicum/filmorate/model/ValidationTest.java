package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void validateFilmNameIsEmptyShouldThrowException() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setDescription("Test description");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createFilm(film));
        assertEquals("Film name can't be empty", exception.getMessage());
    }

    @Test
    void validateFilmReleaseDateBeforeThresholdShouldThrowException() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(120);
        film.setDescription("Test description");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createFilm(film));
        assertEquals("Release date can't be earlier than December 28, 1895.", exception.getMessage());
    }

    @Test
    void validateFilmDurationIsNegativeShouldThrowException() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);
        film.setDescription("Test description");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createFilm(film));
        assertEquals("The duration must be a positive number.", exception.getMessage());
    }

    @Test
    void validateUserLoginIsEmptyShouldThrowException() {
        UserController controller = new UserController();
        User user = new User();
        user.setLogin("");
        user.setEmail("user@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createUser(user));
        assertEquals("Login can't be empty and contain spaces", exception.getMessage());
    }

    @Test
    void validateUserEmailDoesNotContainAtShouldThrowException() {
        UserController controller = new UserController();
        User user = new User();
        user.setLogin("userlogin");
        user.setEmail("userexample.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createUser(user));
        assertEquals("Login can't be empty and must contain @", exception.getMessage());
    }

    @Test
    void validateUserBirthdayIsInFutureShouldThrowException() {
        UserController controller = new UserController();
        User user = new User();
        user.setLogin("userlogin");
        user.setEmail("user@example.com");
        user.setBirthday(LocalDate.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.createUser(user));
        assertEquals("Birthday can't be in future", exception.getMessage());
    }
}