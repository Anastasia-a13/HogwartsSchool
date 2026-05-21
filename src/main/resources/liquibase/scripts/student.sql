--liquibase formatted sql

--changeset avtaeva:create-student-name-index
CREATE INDEX idx_student_name ON student (name);