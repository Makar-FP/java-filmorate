package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mpa")
@RestController
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<List<Mpa>> getAllMpa() {
        List<Mpa> mpaList = filmService.findAllMpa();
        return ResponseEntity.ok(mpaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMpaById(@PathVariable("id") int id) {
        Mpa mpa = filmService.findMpa(id);
        if (mpa != null) {
            return ResponseEntity.ok(mpa);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "MPA rating not found"));
        }
    }
}
