package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Test
    void createFaculty_ReturnsOk() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("red");
        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(1L);
        savedFaculty.setName("Gryffindor");
        savedFaculty.setColor("red");
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(savedFaculty);
        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Gryffindor\", \"color\": \"red\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void getFacultyInfo_ReturnsOk() throws Exception {
        long facultyId = 1L;
        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setName("Slytherin");
        faculty.setColor("green");
        when(facultyService.findFaculty(facultyId)).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", facultyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.name").value("Slytherin"))
                .andExpect(jsonPath("$.color").value("green"));
    }

    @Test
    void getFacultyInfo_NotFound_Returns404() throws Exception {
        long nonExistentId = 999L;
        when(facultyService.findFaculty(nonExistentId))
                .thenThrow(new FacultyNotFoundException("Факультет с id " + nonExistentId + " не найден"));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Факультет с id 999 не найден"));
    }

    @Test
    void editFaculty_ReturnsOk() throws Exception {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("Ravenclaw");
        updatedFaculty.setColor("blue");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(updatedFaculty);
        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"Ravenclaw\", \"color\": \"blue\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravenclaw"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @Test
    void deleteFaculty_ReturnsOk() throws Exception {
        long facultyId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/{id}", facultyId))
                .andExpect(status().isOk());
        verify(facultyService, times(1)).deleteFaculty(facultyId);
    }

    @Test
    void findFaculties_ReturnsOk() throws Exception {
        String query = "Gryffindor";
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Gryffindor");
        faculty1.setColor("red");
        when(facultyService.findFacultyByNameOrColor(query)).thenReturn(List.of(faculty1));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    void getFacultyStudents_ReturnsOk() throws Exception {
        long facultyId = 1L;
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Neville Longbottom");
        student1.setAge(16);
        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setName("Gryffindor");
        faculty.setStudents(List.of(student1));
        when(facultyService.findFaculty(facultyId)).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", facultyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Neville Longbottom"))
                .andExpect(jsonPath("$[0].age").value(16));
    }
}