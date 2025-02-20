package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id;
    private String name;

    public Genre setId(Integer id) {
        this.id = id;
        return this;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }
}
