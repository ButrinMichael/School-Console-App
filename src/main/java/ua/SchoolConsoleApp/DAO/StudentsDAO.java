package ua.SchoolConsoleApp.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.SchoolConsoleApp.Student;
import ua.SchoolConsoleApp.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentsDAO implements Dao<Student> {
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_STUDENT_SQL = "INSERT INTO school.STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?)";
	private static final String UPDATE_STUDENT_SQL = "UPDATE school.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";
	private static final String DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL = "DELETE FROM School.STUDENTS_COURSES WHERE student_id = ?";
	private static final String DELETE_STUDENT_BY_ID_SQL = "DELETE FROM school.students WHERE student_id = ?";
	private static final String SELECT_STUDENT_BY_ID_SQL = "SELECT * FROM school.students WHERE student_id = ?";
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
		jdbcTemplate.update(INSERT_STUDENT_SQL, student.getGroupId(), student.getFirstName(), student.getLastName());
	}

	@Override
	public void update(Student student) {
		jdbcTemplate.update(UPDATE_STUDENT_SQL, student.getGroupId(), student.getFirstName(), student.getLastName(),
				student.getId());
	}

	@Override
	@Transactional
	public void delete(int id) {		
			jdbcTemplate.update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, id);
			jdbcTemplate.update(DELETE_STUDENT_BY_ID_SQL, id);		
	}
	@Override
	public Optional<Student> read(int id) {
		
			List<Student> students = jdbcTemplate.query(SELECT_STUDENT_BY_ID_SQL, studentRowMapper, id);
			if (students.isEmpty()) {

				System.out.println("Student with ID " + id + " does not exist.");
				return Optional.empty();
			}
			return Optional.of(students.get(0));
		} 

	@Override
	public List<Student> getAll() {
		return jdbcTemplate.query(SELECT_STUDENT_BY_ID_SQL, studentRowMapper);
	}

	public int getNumStudentsInGroup(int groupId) {
		try {
			return jdbcTemplate.queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, groupId);
		} catch (EmptyResultDataAccessException e) {
			System.err.println("No students found for group with ID " + groupId);
			return 0;
		} catch (DataAccessException e) {
			System.err
					.println("Error fetching number of students for group with ID " + groupId + ": " + e.getMessage());
			throw new RuntimeException("Failed to fetch number of students", e);
		}
	}

	public List<Student> getStudentsByCourseName(String courseName) {
		try {
			return jdbcTemplate.query(SELECT_STUDENTS_BY_COURSE_NAME_SQL, studentRowMapper, courseName);
		} catch (DataAccessException e) {
			System.err.println("Failed to retrieve students for course name \"" + courseName + "\": " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public int getStudentIdByName(String firstName, String lastName) {
		try {
			return jdbcTemplate.queryForObject(SELECT_STUDENTS_ID_BY_NAME_SQL, Integer.class, firstName, lastName);
		} catch (EmptyResultDataAccessException e) {
			System.err.println("The student with the specified name and surname was not found.");
			return -1;
		} catch (DataAccessException e) {
			System.err.println("Failed to retrieve Student Id" + e.getMessage());
			return -1;
		}
	}

	public void removeStudentFromCourse(int studentId, int courseId) {
		int rowsAffected = jdbcTemplate.update(DELETE_STUDENT_FROM_CURSE_SQL, studentId, courseId);
		if (rowsAffected > 0) {
			System.out.println("Student successfully removed from the course!");
		} else {
			System.out.println("Failed to remove student from the course.");
		}
	}

	public void removeCourseFromStudent(int studentId, int courseId) {
		int rowsAffected = jdbcTemplate.update(DELETE_COURSE_FROM_STUDENT_SQL, studentId, courseId);
		if (rowsAffected > 0) {
			System.out.println("Course successfully removed from the student!");
		} else {
			System.out.println("Failed to remove course from the student.");
		}
	}

	public int addCourseToStudent(int studentId, int courseId) {
		return jdbcTemplate.update(INSERT_COURSE_TO_STUDENT_SQL, studentId, courseId);
	}

	public List<Course> getCoursesByStudentId(int studentId) {
		return jdbcTemplate.query(SELECT_COURSE_BY_STUDENT_ID_SQL, courseRowMapper, studentId);
	}

	public List<Student> getStudentsByCourseId(int courseId) {
		return jdbcTemplate.query(SELECT_STUDENT_BY_COURSE_ID_SQL, studentRowMapper, courseId);
	}

}
