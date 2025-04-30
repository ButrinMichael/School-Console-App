package ua.schoolconsoleapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
=======
import ua.schoolconsoleapp.dao.JPACourseDAO;
import ua.schoolconsoleapp.dao.JPAStudentDAO;
>>>>>>> refs/remotes/origin/main
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;
<<<<<<< HEAD
import ua.schoolconsoleapp.repositories.CourseRepository;
import ua.schoolconsoleapp.repositories.StudentRepository;
=======

>>>>>>> refs/remotes/origin/main
import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

<<<<<<< HEAD
	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
=======
	 private final JPACourseDAO courseDAO;
	    private final JPAStudentDAO studentDAO;
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}
=======
	    public StudentServiceImpl(JPACourseDAO courseDAO, JPAStudentDAO studentDAO) {
	        this.courseDAO = courseDAO;
	        this.studentDAO = studentDAO;
	    }
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
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
=======
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
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	@Override
	public void addNewStudent(Student student) {
		logger.info("Adding new student: {} {}", student.getFirstName(), student.getLastName());
		if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
			throw new IllegalArgumentException("First and last name must not be empty");
		}
		studentRepository.save(student);
	}
=======
	        Student student = studentDAO.findByNameAndLastName(firstName, lastName)
	                .orElseThrow(() -> new RuntimeException("Student not found"));

	        Course course = courseDAO.findByName(courseName)
	                .orElseThrow(() -> new RuntimeException("Course not found"));
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
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
=======
	        if (student.getCourses().contains(course)) {
	            throw new RuntimeException("Student already enrolled in course");
	        }

	        student.getCourses().add(course);
	        course.getStudents().add(student);

	        studentDAO.update(student); 
	    }
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	@Override
	    @Transactional
	    public void addStudentToCourse(String firstName, String lastName, String courseName) {
	        logger.info("Adding student {} {} to course '{}'", firstName, lastName, courseName);
=======
	    @Override
	    @Transactional
	    public void removeStudentFromCourse(String firstName, String lastName, String courseName) {
	        logger.info("Removing student {} {} from course '{}'", firstName, lastName, courseName);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	        Student student = studentRepository.findWithCoursesByFirstNameAndLastName(firstName, lastName)
=======
	        Student student = studentDAO.findByNameAndLastName(firstName, lastName)
>>>>>>> refs/remotes/origin/main
	                .orElseThrow(() -> new RuntimeException("Student not found"));

<<<<<<< HEAD
	        Course course = courseRepository.findByName(courseName)
=======
	        Course course = courseDAO.findByName(courseName)
>>>>>>> refs/remotes/origin/main
	                .orElseThrow(() -> new RuntimeException("Course not found"));

<<<<<<< HEAD
	        if (student.getCourses().contains(course)) {
	            throw new RuntimeException("Student already enrolled in course");
	        }

	        student.getCourses().add(course);
	        course.getStudents().add(student);
	        
	        studentRepository.save(student);	        
	    }
=======
	        if (!student.getCourses().contains(course)) {
	            throw new RuntimeException("Student is not enrolled in course");
	        }
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	@Override
	@Transactional
	public void removeStudentFromCourse(String firstName, String lastName, String courseName) {
		logger.info("Removing student {} {} from course '{}'", firstName, lastName, courseName);
=======
	        student.getCourses().remove(course);
	        course.getStudents().remove(student);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
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
=======
	        studentDAO.update(student);
	    }
>>>>>>> refs/remotes/origin/main

}
