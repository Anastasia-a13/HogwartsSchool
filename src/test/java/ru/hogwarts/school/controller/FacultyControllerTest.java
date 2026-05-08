package ru.hogwarts.school.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

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
    void getFacultyInfo_ReturnsOk() {
        ResponseEntity<Faculty> response = testRestTemplate.getForEntity("/faculty/" + createdFacultyId, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
    }

    @Test
    void getFacultyInfo_NotFound_Returns404() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/faculty/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createFaculty_ReturnsCreated() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        ResponseEntity<Faculty> response = testRestTemplate.postForEntity("/faculty", faculty, Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ravenclaw", response.getBody().getName());
    }

    @Test
    void editFaculty_ReturnsOk() {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(createdFacultyId);
        updatedFaculty.setName("Slytherin");
        updatedFaculty.setColor("Green");

        ResponseEntity<Faculty> response = testRestTemplate.exchange(
                "/faculty",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedFaculty),
                Faculty.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Slytherin", response.getBody().getName());
    }

    @Test
    void deleteFaculty_ReturnsOk() {
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/faculty/" + createdFacultyId,
                org.springframework.http.HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<String> getResponse = testRestTemplate.getForEntity(
                "/faculty/" + createdFacultyId, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void findFaculties_ReturnsOk() {
        ResponseEntity<Collection<Faculty>> responseByName = testRestTemplate.exchange(
                "/faculty/search?query=Gryffindor",
                org.springframework.http.HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, responseByName.getStatusCode());
        assertNotNull(responseByName.getBody());
        assertTrue(responseByName.getBody().stream()
                        .anyMatch(f -> f.getName().contains("Gryffindor")),
                "Должен найти факультет с именем Gryffindor"
        );

        ResponseEntity<Collection<Faculty>> responseByColor = testRestTemplate.exchange(
                "/faculty/search?query=red",
                HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, responseByColor.getStatusCode());
        assertNotNull(responseByColor.getBody());
        assertTrue(responseByColor.getBody().stream()
                        .anyMatch(f -> f.getColor().contains("red")),
                "Должен найти факультет красного цвета"
        );
    }

    @Test
    void getFacultyStudents_ReturnsOk() {
        ResponseEntity<List<Student>> response = testRestTemplate.exchange(
                "/faculty/" + createdFacultyId + "/students",
                org.springframework.http.HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}