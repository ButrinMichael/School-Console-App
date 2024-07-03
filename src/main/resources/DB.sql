DROP TABLE IF EXISTS School.STUDENTS_COURSES;
DROP TABLE IF EXISTS school.COURSES;
DROP TABLE IF EXISTS school.STUDENTS;
DROP TABLE IF EXISTS school.GROUPS;

CREATE TABLE school.GROUPS (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(255)
);

CREATE TABLE school.COURSES (
    course_id SERIAL PRIMARY KEY,
    course_name VARCHAR(255),
    course_description VARCHAR(255)
);

CREATE TABLE school.STUDENTS (
    student_id SERIAL PRIMARY KEY,
    group_id INTEGER REFERENCES school.GROUPS(group_id),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

CREATE TABLE School.STUDENTS_COURSES (
    student_id INTEGER REFERENCES School.STUDENTS(student_id),
    course_id INTEGER REFERENCES School.COURSES(course_id),
    PRIMARY KEY (student_id, course_id)
);

