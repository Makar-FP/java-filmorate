package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    T create(T entinty);
    T getById(long id);
    List<T> getAll();
    T update(T entity);
}
