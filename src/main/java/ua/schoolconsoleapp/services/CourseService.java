package ua.schoolconsoleapp.services;

import ua.schoolconsoleapp.models.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<Course> getAllCourses();

    Optional<Course> findByName(String courseName);

    List<Course> findCoursesByStudentId(int studentId);
}