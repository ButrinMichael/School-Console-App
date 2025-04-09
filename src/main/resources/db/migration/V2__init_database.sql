CREATE SCHEMA IF NOT EXISTS school;

CREATE TABLE IF NOT EXISTS school.GROUPS (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS school.COURSES (
    course_id SERIAL PRIMARY KEY,
    course_name VARCHAR(255),
    course_description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS school.STUDENTS (
    student_id SERIAL PRIMARY KEY,
    group_id INTEGER REFERENCES school.GROUPS(group_id),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS School.STUDENTS_COURSES (
    student_id INTEGER REFERENCES School.STUDENTS(student_id),
    course_id INTEGER REFERENCES School.COURSES(course_id),
    PRIMARY KEY (student_id, course_id)
);
