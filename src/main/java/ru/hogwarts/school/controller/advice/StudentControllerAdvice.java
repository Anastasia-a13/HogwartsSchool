package ru.hogwarts.school.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.exception.InvalidStudentDataException;
import ru.hogwarts.school.exception.StudentNotFoundExeption;

@ControllerAdvice
public class StudentControllerAdvice {
    @ExceptionHandler(StudentNotFoundExeption.class)
    public ResponseEntity<String> handleStudentNotFoundExeption(StudentNotFoundExeption e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidStudentDataException.class)
    public ResponseEntity<String> handleInvalidStudentDataException(InvalidStudentDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
