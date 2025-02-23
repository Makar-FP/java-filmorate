package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Mpa {
    private int id;
    private String name;

    @JsonIgnore
    private String description;

    public Mpa setId(Integer id) {
        this.id = id;
        return this;
    }

    public Mpa setName(String name) {
        this.name = name;
        return this;
    }
}

