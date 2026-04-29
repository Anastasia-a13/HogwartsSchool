package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void getStudentInfo_ReturnsOk() throws Exception {
        long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setName("Harry Potter");
        student.setAge(17);
        when(studentService.findStudent(studentId)).thenReturn(student);
        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentInfo_NotFound_Returns404() throws Exception {
        long nonExistentId = 999L;
        when(studentService.findStudent(nonExistentId))
                .thenThrow(new StudentNotFoundException("Студент с id " + nonExistentId + " не найден"));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Студент с id 999 не найден"));
    }

    @Test
    void createStudent_ReturnsOk() throws Exception {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(16);
        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Hermione Granger");
        savedStudent.setAge(16);
        when(studentService.addStudent(any(Student.class))).thenReturn(savedStudent);
        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Hermione Granger\", \"age\": 16}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hermione Granger"));
    }

    @Test
    void editStudent_ReturnsOk() throws Exception {
        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("Ron Weasley");
        updatedStudent.setAge(17);
        when(studentService.editStudent(any(Student.class))).thenReturn(updatedStudent);
        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"Ron Weasley\", \"age\": 17}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ron Weasley"));
    }

    @Test
    void deleteStudent_ReturnsOk() throws Exception {
        long studentId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", studentId))
                .andExpect(status().isOk());
        verify(studentService, times(1)).deleteStudent(studentId);
    }

    @Test
    void findStudentsByAge_ReturnsOk() throws Exception {
        int age = 17;
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Harry Potter");
        student1.setAge(age);
        when(studentService.findByAge(age)).thenReturn(List.of(student1));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter/by-age")
                        .param("age", String.valueOf(age))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(age));
    }

    @Test
    void getStudentsByAgeRange_ReturnsOk() throws Exception {
        int minAge = 16;
        int maxAge = 18;
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Harry Potter");
        student1.setAge(17);
        when(studentService.findByAgeBetween(minAge, maxAge)).thenReturn(List.of(student1));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter/age-range")
                        .param("min", String.valueOf(minAge))
                        .param("max", String.valueOf(maxAge))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getStudentFaculty_ReturnsOk() throws Exception {
        long studentId = 1L;
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        Student student = new Student();
        student.setId(studentId);
        student.setFaculty(faculty);
        when(studentService.findStudent(studentId)).thenReturn(student);
        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }
}