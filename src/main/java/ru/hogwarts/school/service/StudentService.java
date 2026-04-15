package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            throw new IllegalArgumentException("Имя студента не может быть пустым");
        }
        if (student.getAge() < 11) {
            throw new IllegalArgumentException("Возраст студента не может быть меньше 11 лет");
        }
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id " + id + " не найден"));
    }

    public Student editStudent(Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            throw new IllegalArgumentException("Имя студента не может быть пустым");
        }
        if (student.getAge() < 11) {
            throw new IllegalArgumentException("Возраст студента не может быть меньше 11 лет");
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        if (age < 11) {
            throw new IllegalArgumentException("Возраст должен быть не меньше 11 лет");
        }
        return studentRepository.findByAge(age);
    }
}