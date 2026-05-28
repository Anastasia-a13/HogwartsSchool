package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public Student editStudent(@RequestBody Student student) {
        return studentService.editStudent(student);
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/filter/by-age")
    public Collection<Student> findStudents(@RequestParam(required = false) int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/filter/age-range")
    public Collection<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        return student.getFaculty();
    }

    @GetMapping("/count")
    public long getStudentCount() {
        return studentRepository.countAllStudents();
    }

    @GetMapping("/average-age")
    public double getAverageStudentAge() {
        return studentService.getAverageStudentAge();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentRepository.findLastFiveStudents();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/print-parallel")
    public String printStudentsParallel() {
        studentService.printStudentsParallel();
        return "Started parallel printing of student names";
    }

    @GetMapping("/print-synchronized")
    public String printStudentsSynchronized() {
        studentService.printStudentsSynchronized();
        return "Completed synchronized printing of student names";
    }
}