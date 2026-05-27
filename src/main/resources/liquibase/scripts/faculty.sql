--liquibase formatted sql

--changeset avtaeva:create-faculty-name-color-index
CREATE INDEX idx_faculty_name_color ON faculty (name, color);