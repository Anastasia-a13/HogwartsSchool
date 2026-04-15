package ru.hogwarts.school.exception;

public class StudentNotFoundExeption extends RuntimeException {
    public StudentNotFoundExeption(String message) {
        super(message);
    }
}
