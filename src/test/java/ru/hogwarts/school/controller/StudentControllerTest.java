package ru.hogwarts.school.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {
    @Autowired
    private StudentController studentController;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private Student testStudent;
    private Faculty testFaculty;
    private Long createdStudentId;
    private Long createdFacultyId;

    @BeforeEach
    void setUp() {
        testFaculty = new Faculty();
        testFaculty.setName("Gryffindor");
        testFaculty.setColor("red");

        ResponseEntity<Faculty> facultyResponse = testRestTemplate.postForEntity(
                "/faculty", testFaculty, Faculty.class
        );
        assertEquals(HttpStatus.OK, facultyResponse.getStatusCode());
        createdFacultyId = facultyResponse.getBody().getId();
        testFaculty = facultyResponse.getBody();
        createdFacultyId = testFaculty.getId();

        testStudent = new Student();
        testStudent.setName("Harry Potter");
        testStudent.setAge(17);
        testStudent.setFaculty(testFaculty);

        ResponseEntity<Student> studentResponse = testRestTemplate.postForEntity(
                "/student", testStudent, Student.class
        );
        assertEquals(HttpStatus.OK, studentResponse.getStatusCode());
        createdStudentId = studentResponse.getBody().getId();
    }

    @AfterEach
    void tearDown() {
        if (createdStudentId != null) {
            testRestTemplate.delete("/student/" + createdStudentId);
        }
        if (createdFacultyId != null) {
            testRestTemplate.delete("/faculty/" + createdFacultyId);
        }
    }

    @Test
    public void studentControllerTest() {
        Assertions.assertNotNull(studentController);
    }

    @Test
    void getStudentInfo_ReturnsOk() {
        ResponseEntity<Student> getResponse = testRestTemplate.getForEntity(
                "/student/" + createdStudentId, Student.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Harry Potter", getResponse.getBody().getName());
    }

    @Test
    void getStudentInfo_NotFound_Returns404() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/student/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createStudent_ReturnsOk() {
        Student newStudent = new Student();
        newStudent.setName("Ron Weasley");
        newStudent.setAge(16);
        newStudent.setFaculty(testFaculty);

        ResponseEntity<Student> response = testRestTemplate.postForEntity(
                "/student", newStudent, Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ron Weasley", response.getBody().getName());
    }

    @Test
    void editStudent_ReturnsOk() {
        Student updatedStudent = new Student();
        updatedStudent.setId(createdStudentId);
        updatedStudent.setName("Harry James Potter");
        updatedStudent.setAge(18);

        ResponseEntity<Student> response = testRestTemplate.exchange(
                "/student",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedStudent),
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Harry James Potter", response.getBody().getName());
        assertEquals(18, response.getBody().getAge());
    }

    @Test
    void deleteStudent_ReturnsOk() {
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/student/" + createdStudentId,
                org.springframework.http.HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<String> getResponse = testRestTemplate.getForEntity(
                "/student/" + createdStudentId, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void findStudentsByAge_ReturnsOk() {
        Student student1 = new Student();
        student1.setName("Ron Weasley");
        student1.setAge(17);
        student1.setFaculty(testFaculty);
        testRestTemplate.postForEntity("/student", student1, Student.class);

        Student student2 = new Student();
        student2.setName("Neville Longbottom");
        student2.setAge(17);
        student2.setFaculty(testFaculty);
        testRestTemplate.postForEntity("/student", student2, Student.class);

        ResponseEntity<Collection<Student>> response = testRestTemplate.exchange(
                "/student/filter/by-age?age=17",
                org.springframework.http.HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().stream()
                        .allMatch(s -> s.getAge() == 17),
                "Все студенты должны быть возраста 17 лет"
        );
    }

    @Test
    void getStudentsByAgeRange_ReturnsOk() {
        Student youngStudent = new Student();
        youngStudent.setName("Luna Lovegood");
        youngStudent.setAge(15);
        youngStudent.setFaculty(testFaculty);
        testRestTemplate.postForEntity("/student", youngStudent, Student.class);

        Student oldStudent = new Student();
        oldStudent.setName("Hermione Granger");
        oldStudent.setAge(19);
        oldStudent.setFaculty(testFaculty);
        testRestTemplate.postForEntity("/student", oldStudent, Student.class);

        ResponseEntity<Collection<Student>> response = testRestTemplate.exchange(
                "/student/filter/age-range?min=16&max=20",
                org.springframework.http.HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().stream()
                        .allMatch(s -> s.getAge() >= 16 && s.getAge() <= 20),
                "Все студенты должны быть в диапазоне 16–20 лет"
        );
    }

    @Test
    void getStudentFaculty_ReturnsOk() {
        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(
                "/student/" + createdStudentId + "/faculty", Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
    }
}