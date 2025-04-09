package ua.schoolconsoleapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.dao.JPACourseDAO;
import ua.schoolconsoleapp.dao.JPAStudentDAO;
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;

import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

	 private final JPACourseDAO courseDAO;
	    private final JPAStudentDAO studentDAO;

	    public StudentServiceImpl(JPACourseDAO courseDAO, JPAStudentDAO studentDAO) {
	        this.courseDAO = courseDAO;
	        this.studentDAO = studentDAO;
	    }

	    @Override
	    public List<Student> findStudentsByCourseName(String courseName) {
	        logger.info("Finding students enrolled in course: {}", courseName);
	        return studentDAO.getStudentsByCourseName(courseName);
	    }

	    @Override
	    public void addNewStudent(Student student) {
	        logger.info("Adding new student: {} {}", student.getFirstName(), student.getLastName());
	        if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
	            throw new IllegalArgumentException("First and last name must not be empty");
	        }
	        studentDAO.create(student);
	    }

	    @Override
	    @Transactional
	    public void deleteStudentById(int studentId) {
	        logger.info("Deleting student with ID: {}", studentId);
	        Optional<Student> studentOpt = studentDAO.read(studentId);
	        studentOpt.ifPresentOrElse(student -> {
	            studentDAO.delete(studentId);
	            logger.info("Student {} {} successfully deleted.", student.getFirstName(), student.getLastName());
	        }, () -> {
	            throw new RuntimeException("Student not found with ID: " + studentId);
	        });
	    }


	    @Override
	    @Transactional
	    public void addStudentToCourse(String firstName, String lastName, String courseName) {
	        logger.info("Adding student {} {} to course '{}'", firstName, lastName, courseName);

	        Student student = studentDAO.findByNameAndLastName(firstName, lastName)
	                .orElseThrow(() -> new RuntimeException("Student not found"));

	        Course course = courseDAO.findByName(courseName)
	                .orElseThrow(() -> new RuntimeException("Course not found"));

	        if (student.getCourses().contains(course)) {
	            throw new RuntimeException("Student already enrolled in course");
	        }

	        student.getCourses().add(course);
	        course.getStudents().add(student);

	        studentDAO.update(student); 
	    }

	    @Override
	    @Transactional
	    public void removeStudentFromCourse(String firstName, String lastName, String courseName) {
	        logger.info("Removing student {} {} from course '{}'", firstName, lastName, courseName);

	        Student student = studentDAO.findByNameAndLastName(firstName, lastName)
	                .orElseThrow(() -> new RuntimeException("Student not found"));

	        Course course = courseDAO.findByName(courseName)
	                .orElseThrow(() -> new RuntimeException("Course not found"));

	        if (!student.getCourses().contains(course)) {
	            throw new RuntimeException("Student is not enrolled in course");
	        }

	        student.getCourses().remove(course);
	        course.getStudents().remove(student);

	        studentDAO.update(student);
	    }

}
