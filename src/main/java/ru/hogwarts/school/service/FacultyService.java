package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for add faculty");
        logger.debug("Adding faculty with data: name={}, color={}", faculty.getName(), faculty.getColor());
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            logger.error("Invalid faculty data: name is empty or blank");
            throw new InvalidFacultyDataException("Имя факультета не может быть пустым");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            logger.error("Invalid faculty color: color is empty or blank");
            throw new InvalidFacultyDataException("Цвет факультета не может быть пустым");
        }
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.debug("Faculty successfully added with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for find faculty by id");
        logger.debug("Searching faculty with id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Faculty with id={} not found", id);
                    return new FacultyNotFoundException("Факультет с id " + id + " не найден");
                });
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for edit faculty");
        logger.debug("Editing faculty with id: {}, data: name={}, color={}",
                faculty.getId(), faculty.getName(), faculty.getColor());
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            logger.error("Invalid faculty data during edit: name is empty or blank for faculty id={}", faculty.getId());
            throw new InvalidFacultyDataException("Имя факультета не может быть пустым");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            logger.error("Invalid faculty color during edit: color is empty or blank for faculty id={}", faculty.getId());
            throw new InvalidFacultyDataException("Цвет факультета не может быть пустым");
        }
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.debug("Faculty with id={} successfully updated", updatedFaculty.getId());
        return updatedFaculty;
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty");
        logger.warn("Deleting faculty with id: {}", id);
        facultyRepository.deleteById(id);
        logger.debug("Faculty with id={} successfully deleted", id);
    }

    public Collection<Faculty> findFacultyByNameOrColor(String query) {
        logger.info("Was invoked method for find faculties by name or color");
        logger.debug("Searching faculties with query: {}", query);
        Collection<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(query, query);
        logger.debug("Found {} faculties for query: {}", faculties.size(), query);
        return faculties;
    }
}