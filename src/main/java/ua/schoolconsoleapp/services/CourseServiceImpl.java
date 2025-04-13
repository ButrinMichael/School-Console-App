package ua.schoolconsoleapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.repositories.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getAllCourses() {
        logger.info("Fetching all courses from repository");
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> findByName(String courseName) {
        logger.info("Searching for course with name: {}", courseName);
        return courseRepository.findByName(courseName);
    }

    @Override
    public List<Course> findCoursesByStudentId(int studentId) {
        logger.info("Fetching courses for student ID: {}", studentId);
        return courseRepository.findCoursesByStudentId(studentId);
    }
}
