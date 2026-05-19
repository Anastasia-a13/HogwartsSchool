SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM student s
         INNER JOIN faculty f ON s.faculty_id = f.id;
SELECT
    s.name AS student_name,
    s.age AS student_age
FROM student s
         INNER JOIN avatar a ON s.id = a.student_id;