package ua.schoolconsoleapp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentsDAO implements Dao<Student> {
	private static final Logger logger = LoggerFactory.getLogger(StudentsDAO.class);
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_STUDENT_SQL = "INSERT INTO school.STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?)";
	private static final String UPDATE_STUDENT_SQL = "UPDATE school.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";
	private static final String DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL = "DELETE FROM School.STUDENTS_COURSES WHERE student_id = ?";
	private static final String DELETE_STUDENT_BY_ID_SQL = "DELETE FROM school.students WHERE student_id = ?";
	private static final String SELECT_STUDENT_BY_ID_SQL = "SELECT * FROM school.students WHERE student_id = ?";
	private static final String SELECT_ALL_STUDENTS_SQL = "SELECT * FROM school.students";
	private static final String SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL = "SELECT COUNT(*) FROM school.students WHERE group_id = ?";
	private static final String SELECT_STUDENTS_BY_COURSE_NAME_SQL = "SELECT s.* FROM school.students s "
			+ "JOIN school.students_courses sc ON s.student_id = sc.student_id "
			+ "JOIN school.courses c ON sc.course_id = c.course_id " + "WHERE c.course_name = ?";
	private static final String SELECT_STUDENTS_ID_BY_NAME_SQL = "SELECT student_id FROM school.students WHERE first_name = ? AND last_name = ? AND (group_id IS NULL OR group_id IS NOT NULL)";
	private static final String DELETE_STUDENT_FROM_CURSE_SQL = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
	private static final String DELETE_COURSE_FROM_STUDENT_SQL = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
	private static final String INSERT_COURSE_TO_STUDENT_SQL = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
	private static final String SELECT_COURSE_BY_STUDENT_ID_SQL = "SELECT c.* FROM school.courses c "
			+ "JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
	private static final String SELECT_STUDENT_BY_COURSE_ID_SQL = "SELECT s.* FROM school.students s "
			+ "JOIN school.students_courses sc ON s.student_id = sc.student_id " + "WHERE sc.course_id = ?";

	@Autowired
	public StudentsDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private final RowMapper<Student> studentRowMapper = new RowMapper<Student>() {
		@Override
		public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("student_id");
			int groupId = rs.getInt("group_id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			return new Student(id, groupId, firstName, lastName);
		}
	};

	private final RowMapper<Course> courseRowMapper = new RowMapper<Course>() {
		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			int courseId = rs.getInt("course_id");
			String courseName = rs.getString("course_name");
			return new Course(courseId, courseName);
		}
	};

	@Override
	public void create(Student student) {
		logger.info("Creating student: {} {}", student.getFirstName(), student.getLastName());
		try {
			jdbcTemplate.update(INSERT_STUDENT_SQL, student.getGroupId(), student.getFirstName(),
					student.getLastName());
			logger.info("Student {} {} created successfully.", student.getFirstName(), student.getLastName());
		} catch (DataAccessException e) {
			logger.error("Failed to create student {} {}: {}", student.getFirstName(), student.getLastName(),
					e.getMessage(), e);
			throw new RuntimeException("Failed to create student", e);
		}
	}

	@Override
	public void update(Student student) {
		logger.info("Updating student with ID: {}", student.getId());
		try {
			jdbcTemplate.update(UPDATE_STUDENT_SQL, student.getGroupId(), student.getFirstName(), student.getLastName(),
					student.getId());
			logger.info("Student with ID {} updated successfully.", student.getId());
		} catch (DataAccessException e) {
			logger.error("Failed to update student with ID {}: {}", student.getId(), e.getMessage(), e);
			throw new RuntimeException("Failed to update student", e);
		}
	}

	@Override
	@Transactional
	public void delete(int id) {
		logger.info("Deleting student with ID: {}", id);
		try {
			jdbcTemplate.update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, id);
			jdbcTemplate.update(DELETE_STUDENT_BY_ID_SQL, id);
			logger.info("Student with ID {} deleted successfully.", id);
		} catch (DataAccessException e) {
			logger.error("Failed to delete student with ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Failed to delete student", e);
		}
	}

	@Override
	public Optional<Student> read(int id) {
		logger.info("Reading student with ID: {}", id);
		try {
			List<Student> students = jdbcTemplate.query(SELECT_STUDENT_BY_ID_SQL, studentRowMapper, id);
			if (students.isEmpty()) {
				logger.warn("Student with ID {} does not exist.", id);
				System.out.println("Student with ID " + id + " does not exist.");
				return Optional.empty();
			}
			logger.info("Student with ID {} found.", id);
			return Optional.of(students.get(0));
		} catch (DataAccessException e) {
			logger.error("Failed to read student with ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Failed to read student", e);
		}
	}

	@Override
	public List<Student> getAll() {
		logger.info("Fetching all students");
		 try {
		        logger.info("Fetching all students");
		        return jdbcTemplate.query(SELECT_ALL_STUDENTS_SQL, studentRowMapper);
		    } catch (DataAccessException e) {
			logger.error("Failed to fetch all students: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to fetch students", e);
		}
	}

	public int getNumStudentsInGroup(int groupId) {
		logger.info("Counting students in group with ID: {}", groupId);
		try {
			return jdbcTemplate.queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, groupId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("No students found for group with ID: {}", groupId);
			System.err.println("No students found for group with ID " + groupId);
			return 0;
		} catch (DataAccessException e) {
			logger.error("Failed to count students in group with ID {}: {}", groupId, e.getMessage(), e);
			System.err
					.println("Error fetching number of students for group with ID " + groupId + ": " + e.getMessage());
			throw new RuntimeException("Failed to fetch number of students", e);
		}
	}

	public List<Student> getStudentsByCourseName(String courseName) {
		logger.info("Fetching students for course: {}", courseName);
		try {
			List<Student> students = jdbcTemplate.query(SELECT_STUDENTS_BY_COURSE_NAME_SQL, studentRowMapper,
					courseName);
			logger.info("Found {} students for course '{}'", students.size(), courseName);
			return students;
		} catch (DataAccessException e) {
			logger.error("Failed to fetch students for course '{}': {}", courseName, e.getMessage(), e);
			System.err.println("Failed to retrieve students for course name \"" + courseName + "\": " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public int getStudentIdByName(String firstName, String lastName) {
		logger.info("Fetching student ID for: {} {}", firstName, lastName);
		try {
			return jdbcTemplate.queryForObject(SELECT_STUDENTS_ID_BY_NAME_SQL, Integer.class, firstName, lastName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Student {} {} not found.", firstName, lastName);
			System.err.println("The student with the specified name and surname was not found.");
			return -1;
		} catch (DataAccessException e) {
			logger.error("Failed to fetch student ID for {} {}: {}", firstName, lastName, e.getMessage(), e);
			System.err.println("Failed to retrieve Student Id" + e.getMessage());
			return -1;
		}
	}

	public void removeStudentFromCourse(int studentId, int courseId) {
		logger.info("Removing student with ID {} from course with ID {}", studentId, courseId);
		try {
			int rowsAffected = jdbcTemplate.update(DELETE_STUDENT_FROM_CURSE_SQL, studentId, courseId);
			if (rowsAffected > 0) {
				logger.info("Student with ID {} removed from course with ID {}", studentId, courseId);
				System.out.println("Student successfully removed from the course!");
			} else {
				logger.warn("No records removed for student with ID {} from course with ID {}", studentId, courseId);
				System.out.println("Failed to remove student from the course.");
			}
		} catch (DataAccessException e) {
			logger.error("Failed to remove student with ID {} from course with ID {}: {}", studentId, courseId,
					e.getMessage(), e);
			throw new RuntimeException("Failed to remove student from course", e);
		}
	}

	public void removeCourseFromStudent(int studentId, int courseId) {
		logger.info("Removing course with ID {} from student with ID {}", courseId, studentId);
		try {
			int rowsAffected = jdbcTemplate.update(DELETE_COURSE_FROM_STUDENT_SQL, studentId, courseId);
			if (rowsAffected > 0) {
				logger.info("Course with ID {} removed from student with ID {}", courseId, studentId);
				System.out.println("Course successfully removed from the student!");
			} else {
				logger.warn("No records removed for course with ID {} from student with ID {}", courseId, studentId);
				System.out.println("Failed to remove course from the student.");
			}
		} catch (DataAccessException e) {
			logger.error("Failed to remove course with ID {} from student with ID {}: {}", courseId, studentId,
					e.getMessage(), e);
			throw new RuntimeException("Failed to remove course from student", e);
		}
	}

	public int addCourseToStudent(int studentId, int courseId) {
		logger.info("Adding course with ID {} to student with ID {}", courseId, studentId);
		try {
			return jdbcTemplate.update(INSERT_COURSE_TO_STUDENT_SQL, studentId, courseId);
		} catch (DataAccessException e) {
			logger.error("Failed to add course with ID {} to student with ID {}: {}", courseId, studentId,
					e.getMessage(), e);
			throw new RuntimeException("Failed to add course to student", e);
		}
	}

	public List<Course> getCoursesByStudentId(int studentId) {
		logger.info("Fetching courses for student with ID: {}", studentId);
		try {
			List<Course> courses = jdbcTemplate.query(SELECT_COURSE_BY_STUDENT_ID_SQL, courseRowMapper, studentId);
			logger.info("Found {} courses for student with ID: {}", courses.size(), studentId);
			return courses;
		} catch (DataAccessException e) {
			logger.error("Failed to fetch courses for student with ID {}: {}", studentId, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch courses for student with ID " + studentId, e);
		}
	}

	public List<Student> getStudentsByCourseId(int courseId) {
		logger.info("Fetching students for course with ID: {}", courseId);
		try {
			List<Student> students = jdbcTemplate.query(SELECT_STUDENT_BY_COURSE_ID_SQL, studentRowMapper, courseId);
			logger.info("Found {} students for course with ID: {}", students.size(), courseId);
			return students;
		} catch (DataAccessException e) {
			logger.error("Failed to fetch students for course with ID {}: {}", courseId, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch students for course with ID " + courseId, e);
		}
	}

}
