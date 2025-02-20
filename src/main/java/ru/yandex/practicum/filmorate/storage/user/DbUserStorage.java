package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Primary
@Repository
public class DbUserStorage implements UserStorage {

    private static final String GET_BY_ID = """
            SELECT * FROM users WHERE id = ?
            """;

    private static final String GET_ALL = """
            SELECT * FROM users
            """;

    private static final String INSERT = """
            INSERT INTO users (name, login, email, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE users
            SET name = ?, login = ?, email = ?, birthday = ?
            WHERE id = ?
            """;

    private static final String ADD_FRIEND = """
        MERGE INTO friends (user_id, friend_id, confirmed) VALUES (?, ?, FALSE)
        """;

    private static final String DELETE_FRIEND = """
        DELETE FROM friends WHERE user_id = ? AND friend_id = ?
        """;

    private static final String GET_USER_FRIENDS = """
        SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ?)
        """;

    private static final String GET_COMMON_FRIENDS = """
        SELECT * FROM users WHERE id IN (
            SELECT f1.friend_id FROM friends f1
            INNER JOIN friends f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ?)
        """;

    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT, new String[]{"id"});
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getLogin());
            stmt.setString(3, entity.getEmail());
            stmt.setDate(4, java.sql.Date.valueOf(entity.getBirthday()));
            return stmt;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Ошибка при создании пользователя: не удалось получить ID");
        }

        return entity.setId(key.longValue());
    }

    @Override
    public User getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, getUserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(GET_ALL, getUserMapper());
    }

    @Override
    public User update(User entity) {
        jdbcTemplate.update(UPDATE,
                entity.getName(),
                entity.getLogin(),
                entity.getEmail(),
                java.sql.Date.valueOf(entity.getBirthday()),
                entity.getId());
        return entity;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            getById(friendId);
            getById(userId);
            jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Один из пользователей не найден");
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
            if (rowsAffected == 0) {
                return;
            }
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while removing friend", e);
        }
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        try {
            getById(userId);
            return jdbcTemplate.query(GET_USER_FRIENDS, getUserMapper(), userId);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        try {
            getById(userId);
            getById(friendId);
            return jdbcTemplate.query(GET_COMMON_FRIENDS, getUserMapper(), userId, friendId);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Один из пользователей не найден");
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    private static RowMapper<User> getUserMapper() {
        return (resultSet, rowNum) ->
                new User()
                        .setId(resultSet.getLong("id"))
                        .setName(resultSet.getString("name"))
                        .setLogin(resultSet.getString("login"))
                        .setEmail(resultSet.getString("email"))
                        .setBirthday(resultSet.getDate("birthday").toLocalDate());
    }
}