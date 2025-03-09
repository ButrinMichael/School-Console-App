package ua.schoolconsoleapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.dao.CourseDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.models.Student;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

	private final CourseDAO courseDAO;
	private final StudentsDAO studentsDAO;

	public StudentServiceImpl(CourseDAO courseDAO, StudentsDAO studentsDAO) {
		this.courseDAO = courseDAO;
		this.studentsDAO = studentsDAO;
	}

	@Override
	public List<Student> findStudentsByCourseName(String courseName) {
		logger.info("Search students by course name: {}", courseName);
		List<Student> result = new ArrayList<>();
		int courseId = courseDAO.getCourseIdByName(courseName);

		if (courseId == -1) {
			logger.warn("Course '{}' not found in the database.", courseName);
			return result;
		}

		List<Student> students = studentsDAO.getStudentsByCourseName(courseName);

		if (!students.isEmpty()) {
			logger.info("Found {} students for course '{}'", students.size(), courseName);
			result.addAll(students);
		} else {
			logger.info("No students found for course.", courseName);
		}

		return result;
	}

	@Override
	public void addNewStudent(Student student) {
		logger.info("Adding a new student: {} {}", student.getFirstName(), student.getLastName());

		if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
			logger.warn("Student's first or last name is empty.");
			throw new IllegalArgumentException("Name or surname cannot be empty.");
		}
		studentsDAO.create(student);
		logger.info("Student {} {} added successfully.", student.getFirstName(), student.getLastName());
	}

	@Override
	public void deleteStudentById(int studentId) {
		logger.info("Delete Student with Id: {}", studentId);

		try {
			Optional<Student> studentOpt = studentsDAO.read(studentId);
			if (!studentOpt.isPresent()) {
				logger.warn("Student with ID {} does not found.", studentId);
				throw new RuntimeException("Student with ID " + studentId + " does not exist.");
			}

			studentsDAO.delete(studentId);
			Student student = studentOpt.get();
			logger.info("Student {} {} successfully deleted.", student.getFirstName(), student.getLastName());
			System.out.println("The student " + student.getFirstName() + " " + student.getLastName()
					+ " has been successfully deleted.");
		} catch (RuntimeException e) {
			logger.error("Error deleting student with ID {}: {}", studentId, e.getMessage(), e);
			throw new RuntimeException("Failed to delete student: " + e.getMessage(), e);
		}
	}

	@Override
	public void addStudentToCourse(String studentName, String studentLastName, String courseName) {
		logger.info("Adding student {} {} to course '{}'", studentName, studentLastName, courseName);
		int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
		if (studentId == -1) {
			logger.warn("Student {} {} not found.", studentName, studentLastName);
			throw new RuntimeException("Student not found: " + studentName + " " + studentLastName);
		}

		int courseId = courseDAO.getCourseIdByName(courseName);
		if (courseId == -1) {
			logger.warn("Course '{}' not found.", courseName);
			throw new RuntimeException("Course not found: " + courseName);
		}

		boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
		if (isEnrolled) {
			logger.warn("The student {} {} is already enrolled in the course '{}'", studentName, studentLastName,
					courseName);
			throw new RuntimeException("The student is already enrolled in the course: " + courseName);
		}

		studentsDAO.addCourseToStudent(studentId, courseId);
		logger.info("The student {} {} successfully added to the course '{}'", studentName, studentLastName,
				courseName);
	}

	@Override
	public void removeStudentFromCourse(String studentName, String studentLastName, String courseName) {
		logger.info("Removing student {} {} from a course '{}'", studentName, studentLastName, courseName);

		int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
		if (studentId == -1) {
			logger.warn("Student {} {}  not found.", studentName, studentLastName);
			throw new RuntimeException("Student not found: " + studentName + " " + studentLastName);
		}

		int courseId = courseDAO.getCourseIdByName(courseName);
		if (courseId == -1) {
			logger.warn("Course '{}' not found.", courseName);
			throw new RuntimeException("Course not found: " + courseName);
		}

		boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
		if (!isEnrolled) {
			 logger.warn("The student {} {} is not enrolled in the specified course '{}'", studentName, studentLastName, courseName);
			throw new RuntimeException("The student is not enrolled in the specified course: " + courseName);
		}

		studentsDAO.removeStudentFromCourse(studentId, courseId);
		logger.info("Student {} {} successfully removed from course '{}'", studentName, studentLastName, courseName);
	}

}
