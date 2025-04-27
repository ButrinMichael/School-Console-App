package ua.schoolconsoleapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.repositories.CourseRepository;
import ua.schoolconsoleapp.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;

	public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	@Override
	public List<Course> getCoursesByStudentName(String firstName, String lastName) {
	    Student student = studentRepository.findWithCoursesByFirstNameAndLastName(firstName, lastName)
	            .orElseThrow(() -> new RuntimeException("Student not found"));
	    return new ArrayList<>(student.getCourses());
	}
	
	@Override
	public List<Student> findStudentsByCourseName(String courseName) {
		logger.info("Finding students enrolled in course: {}", courseName);
		return studentRepository.findStudentsByCourseName(courseName);
	}

	@Override
	public void addNewStudent(Student student) {
		logger.info("Adding new student: {} {}", student.getFirstName(), student.getLastName());
		if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
			throw new IllegalArgumentException("First and last name must not be empty");
		}
		studentRepository.save(student);
	}

	@Override
	@Transactional
	public void deleteStudentById(int studentId) {
		logger.info("Deleting student with ID: {}", studentId);
		Optional<Student> studentOpt = studentRepository.findById(studentId);
		studentOpt.ifPresentOrElse(student -> {
			studentRepository.delete(student);
			logger.info("Student {} {} successfully deleted.", student.getFirstName(), student.getLastName());
		}, () -> {
			throw new RuntimeException("Student not found with ID: " + studentId);
		});
	}

	@Override
	    @Transactional
	    public void addStudentToCourse(String firstName, String lastName, String courseName) {
	        logger.info("Adding student {} {} to course '{}'", firstName, lastName, courseName);

	        Student student = studentRepository.findWithCoursesByFirstNameAndLastName(firstName, lastName)
	                .orElseThrow(() -> new RuntimeException("Student not found"));

	        Course course = courseRepository.findByName(courseName)
	                .orElseThrow(() -> new RuntimeException("Course not found"));

	        if (student.getCourses().contains(course)) {
	            throw new RuntimeException("Student already enrolled in course");
	        }

	        student.getCourses().add(course);
	        course.getStudents().add(student);
	        
	        studentRepository.save(student);	        
	    }

	@Override
	@Transactional
	public void removeStudentFromCourse(String firstName, String lastName, String courseName) {
		logger.info("Removing student {} {} from course '{}'", firstName, lastName, courseName);

		Student student = studentRepository.findWithCoursesByFirstNameAndLastName(firstName, lastName)
				.orElseThrow(() -> new RuntimeException("Student not found"));

		Course course = courseRepository.findByName(courseName)
				.orElseThrow(() -> new RuntimeException("Course not found"));

		if (!student.getCourses().contains(course)) {
			throw new RuntimeException("Student is not enrolled in course");
		}

		student.getCourses().remove(course);
		course.getStudents().remove(student);

		 studentRepository.save(student);
	}

}
