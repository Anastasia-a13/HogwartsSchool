package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.InvalidFacultyDataException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            throw new InvalidFacultyDataException("Имя факультета не может быть пустым");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            throw new InvalidFacultyDataException("Цвет факультета не может быть пустым");
        }
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException("Факультет с id " + id + " не найден"));
    }

    public Faculty editFaculty(Faculty faculty) {
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            throw new InvalidFacultyDataException("Имя факультета не может быть пустым");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            throw new InvalidFacultyDataException("Цвет факультета не может быть пустым");
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }
}