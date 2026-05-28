package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.InvalidStudentDataException;
import ru.hogwarts.school.exception.StudentNotFoundExeption;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for add student");
        logger.debug("Adding student with data: name={}, age={}", student.getName(), student.getAge());
        if (student.getName() == null || student.getName().isBlank()) {
            logger.error("Student name is null or blank");
            throw new InvalidStudentDataException("Имя студента не может быть пустым");
        }
        if (student.getAge() < 11) {
            logger.error("Invalid student age: age={} is less than 11", student.getAge());
            throw new InvalidStudentDataException("Возраст студента не может быть меньше 11 лет");
        }
        Student savedStudent = studentRepository.save(student);
        logger.debug("Student successfully added with id: {}", savedStudent.getId());
        return savedStudent;
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student by id");
        logger.debug("Searching student with id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Student with id={} not found", id);
                    return new StudentNotFoundExeption("Студент с id " + id + " не найден");
                });
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        logger.debug("Editing student with id: {}, data: name={}, age={}",
                student.getId(), student.getName(), student.getAge());
        if (student.getName() == null || student.getName().isBlank()) {
            logger.error("Invalid student data during edit: name is empty or blank for student id={}", student.getId());
            throw new InvalidStudentDataException("Имя студента не может быть пустым");
        }
        if (student.getAge() < 11) {
            logger.error("Invalid student age during edit: age={} is less than 11 for student id={}",
                    student.getAge(), student.getId());
            throw new InvalidStudentDataException("Возраст студента не может быть меньше 11 лет");
        }
        Student updatedStudent = studentRepository.save(student);
        logger.debug("Student with id={} successfully updated", updatedStudent.getId());
        return updatedStudent;
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        logger.warn("Deleting student with id: {}", id);
        studentRepository.deleteById(id);
        logger.debug("Student with id={} successfully deleted", id);
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age");
        logger.debug("Searching students with age: {}", age);
        if (age < 11) {
            logger.warn("Attempt to search students with age={}, which is less than minimum allowed age 11", age);
            throw new InvalidStudentDataException("Возраст должен быть не меньше 11 лет");
        }
        Collection<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age={}", students.size(), age);
        return students;
    }

    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for find students by age range");
        logger.debug("Searching students with age between {} and {}", minAge, maxAge);
        if (minAge < 11 || maxAge < 11) {
            logger.warn("Attempt to search students with invalid age range: minAge={}, maxAge={}. Age must be at least 11",
                    minAge, maxAge);
            throw new InvalidStudentDataException("Возраст должен быть не меньше 11 лет");
        }
        Collection<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range {}-{}", students.size(), minAge, maxAge);
        return students;
    }
}