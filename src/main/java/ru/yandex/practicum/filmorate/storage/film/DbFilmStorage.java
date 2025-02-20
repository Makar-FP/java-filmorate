package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
public class DbFilmStorage implements FilmStorage {
    private static final String GET_BY_ID = """
            SELECT f.*,
                   m.id AS mpa_id, m.name AS mpa_name,
                   g.id AS genre_id, g.name AS genre_name
            FROM film f
            LEFT JOIN MPA m ON f.mpa_id = m.id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            WHERE f.id = ?
            """;

    private static final String GET_ALL = """
            SELECT f.*,
            m.id AS mpa_id, m.name AS mpa_name,
            g.id AS genre_id, g.name AS genre_name
            FROM film f
            LEFT JOIN MPA m ON f.mpa_id = m.id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id;
            """;

    private static final String INSERT = """
            INSERT INTO film (name, description, releaseDate, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE film
            SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

    private static final String GET_GENRES_BY_FILM_ID = """
            SELECT g.id, g.name
            FROM films_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id = ?
            """;

    private static final String DELETE_FILM_GENRES = """
            DELETE FROM films_genres WHERE film_id = ?
            """;

    private static final String GET_FILM_GENRES = """
             SELECT g.id, g.name FROM films_genres fg
             JOIN genres g ON fg.genre_id = g.id
             WHERE fg.film_id = ?
             """;

    private static final String ADD_LIKE = """
            MERGE INTO likes USING (VALUES (?, ?)) AS v(user_id, film_id)
            ON likes.user_id = v.user_id AND likes.film_id = v.film_id
            WHEN NOT MATCHED THEN
            INSERT (user_id, film_id) VALUES (v.user_id, v.film_id);
            UPDATE film SET likes = likes + 1 WHERE id = ?;
            """;

    private static final String REMOVE_LIKE = """
            DELETE FROM likes WHERE user_id = ? AND film_id = ?;
            UPDATE film SET likes = likes - 1 WHERE id = ?;
            """;

    private static final String GET_POPULAR = """
            SELECT f.*,
            m.id AS mpa_id, m.name AS mpa_name,
            g.id AS genre_id, g.name AS genre_name,
            COALESCE(l.like_count, 0) AS like_count
                    FROM film f
                    LEFT JOIN MPA m ON f.mpa_id = m.id
                    LEFT JOIN films_genres fg ON f.id = fg.film_id
                    LEFT JOIN genres g ON fg.genre_id = g.id
                    LEFT JOIN (
                        SELECT film_id, COUNT(user_id) AS like_count
                        FROM likes
                        GROUP BY film_id
                    ) l ON f.id = l.film_id
                    ORDER BY like_count DESC
                    LIMIT ?;
            """;

    private static final String GET_ALL_GENRES = "SELECT * FROM genres";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL_MPA = "SELECT * FROM MPA";
    private static final String GET_MPA_BY_ID = "SELECT * FROM MPA WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        if (film.getMpa() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA не может быть null");
        }

        findMpa(film.getMpa().getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                getGenreById(genre.getId());
            }
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при создании фильма");
        }

        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        insertFilmGenres(filmId, new ArrayList<>(film.getGenres()));

        return getById(filmId);
    }

    @Override
    public Film getById(long id) {
        Film film = jdbcTemplate.query(GET_BY_ID, getFilmMapper(), id).stream().findFirst().orElse(null);

        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден, id=" + id);
        }

        film.setGenres(loadGenresForFilm(film.getId()));

        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query(GET_ALL, getFilmMapper());
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(getGenresByFilmId(film.getId()))); // Загружаем жанры
        }
        return films;
    }

    @Override
    public Film update(Film entity) {
        if (entity.getMpa() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA не может быть null");
        }

        findMpa(entity.getMpa().getId());

        if (entity.getGenres() != null) {
            for (Genre genre : entity.getGenres()) {
                getGenreById(genre.getId());
            }
        }
        int rowsUpdated = jdbcTemplate.update(UPDATE,
                entity.getName(),
                entity.getDescription(),
                java.sql.Date.valueOf(entity.getReleaseDate()),
                entity.getDuration(),
                entity.getMpa().getId(),
                entity.getId());

        if (rowsUpdated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден, id=" + entity.getId());
        }

        jdbcTemplate.update(DELETE_FILM_GENRES, entity.getId());
        insertFilmGenres(entity.getId(), new ArrayList<>(entity.getGenres()));

        return getById(entity.getId());
    }

    @Override
    public boolean setLikeFilm(long filmId, long userId) {
        int updatedRows = jdbcTemplate.update(ADD_LIKE, userId, filmId, filmId);
        return updatedRows > 0;
    }

    @Override
    public Film removeLikeFilm(long filmId, long userId) {
        int updatedRows = jdbcTemplate.update(REMOVE_LIKE, userId, filmId, filmId);
        return updatedRows > 0 ? getById(filmId) : null;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(GET_POPULAR, getFilmMapper(), count);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_ALL_GENRES, getGenreMapper());
    }

    @Override
    public Genre getGenreById(int id) {
        return jdbcTemplate.query(GET_GENRE_BY_ID, getGenreMapper(), id)
                .stream().findFirst().orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден жанр id=" + id));
    }

    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query(GET_ALL_MPA, getMpaMapper());
    }

    @Override
    public Mpa findMpa(int id) {
        return jdbcTemplate.query(GET_MPA_BY_ID, getMpaMapper(), id)
                .stream().findFirst().orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден рейтинг id=" + id));
    }

    private static RowMapper<Film> getFilmMapper() {
        return (resultSet, rowNum) -> {
            Film film = new Film()
                    .setId(resultSet.getLong("id"))
                    .setName(resultSet.getString("name"))
                    .setDescription(resultSet.getString("description"))
                    .setReleaseDate(resultSet.getDate("releaseDate").toLocalDate())
                    .setDuration(resultSet.getInt("duration"))
                    .setGenres(new LinkedHashSet<>());

            if (resultSet.getObject("mpa_id") != null) {
                film.setMpa(new Mpa()
                        .setId(resultSet.getInt("mpa_id"))
                        .setName(resultSet.getString("mpa_name")));
            }

            if (resultSet.getObject("genre_id") != null) {
                film.getGenres().add(new Genre()
                        .setId(resultSet.getInt("genre_id"))
                        .setName(resultSet.getString("genre_name")));
            }
            return film;
        };
    }

    private static RowMapper<Genre> getGenreMapper() {
        return (resultSet, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(resultSet.getInt("id"));
            genre.setName(Objects.requireNonNullElse(resultSet.getString("name"), "Неизвестный жанр"));
            return genre;
        };
    }

    private static RowMapper<Mpa> getMpaMapper() {
        return (resultSet, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("id"));
            mpa.setName(Objects.requireNonNullElse(resultSet.getString("name"), "Неизвестный рейтинг"));
            return mpa;
        };
    }

    private List<Genre> getGenresByFilmId(long filmId) {
        return jdbcTemplate.query(GET_FILM_GENRES, getGenreMapper(), filmId);
    }

    private void insertFilmGenres(long filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES " +
                genres.stream().map(g -> "(?, ?)").collect(Collectors.joining(", "));

        List<Object> params = new ArrayList<>();
        genres.forEach(g -> {
            params.add(filmId);
            params.add(g.getId());
        });

        jdbcTemplate.update(sql, params.toArray());
    }

    private LinkedHashSet<Genre> loadGenresForFilm(long filmId) {
        return jdbcTemplate.query(GET_GENRES_BY_FILM_ID, (rs, rowNum) ->
                        new Genre()
                                .setId(rs.getInt("id"))
                                .setName(rs.getString("name")), filmId)
                .stream()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
