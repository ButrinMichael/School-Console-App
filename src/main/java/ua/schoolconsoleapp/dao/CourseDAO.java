package ua.schoolconsoleapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.schoolconsoleapp.entity.Course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseDAO implements Dao<Course> {
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_COURSE_SQL = "INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)";
	private static final String SELECT_COURS_BY_ID_SQL = "SELECT * FROM school.courses WHERE course_id = ?";
	private static final String UPDATE_COURSE_SQL = "UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?";
	private static final String DELETE_STUDENT_COURSE_SQL = "DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?";
	private static final String DELETE_COURSE_SQL = "DELETE FROM School.COURSES WHERE course_id = ?";
	private static final String SELECT_ALL_COURSES_SQL = "SELECT * FROM school.COURSES";
	private static final String SELECT_COURSE_ID_BY_NAME_SQL = "SELECT course_id FROM school.COURSES WHERE course_name = ?";
	private static final String CHECK_STUDENTENROLLMENT_SQL = "SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?";
	private static final String SELECT_COURSE_BY_STUDENT_ID_SQL = "SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
			+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
	private static final String ASSIGN_COURSE_SQL = "INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)";

	@Autowired
	public CourseDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private final RowMapper<Course> courseRowMapper = new RowMapper<Course>() {
		@Override
		public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			int courseId = rs.getInt("course_id");
			String courseName = rs.getString("course_name");
			String descriptionName = rs.getString("course_description");

			return new Course(courseId, courseName, descriptionName);
		}
	};

	@Override
	public void create(Course course) {
		jdbcTemplate.update(INSERT_COURSE_SQL, course.getName(), course.getDescription());
	}

	@Override
	public Optional<Course> read(int id) {
		return jdbcTemplate.query(SELECT_COURS_BY_ID_SQL, courseRowMapper, id).stream().findFirst();
	}

	@Override
	public void update(Course course) {
		jdbcTemplate.update(UPDATE_COURSE_SQL, course.getName(), course.getDescription(), course.getId());
	}

	@Override
	@Transactional
	public void delete(int id) {
		jdbcTemplate.update(DELETE_STUDENT_COURSE_SQL, id);
		jdbcTemplate.update(DELETE_COURSE_SQL, id);
	}

	@Override
	public List<Course> getAll() {
			return jdbcTemplate.query(SELECT_ALL_COURSES_SQL, courseRowMapper);
			}

	public int getCourseIdByName(String courseName) {
		try {
			return jdbcTemplate.queryForObject(SELECT_COURSE_ID_BY_NAME_SQL, Integer.class, courseName);
		} catch (EmptyResultDataAccessException e) {
			System.err.println("Course with name \"" + courseName + "\" not found.");
			return -1;
		} catch (DataAccessException e) {
			System.err.println("Failed to retrieve course ID for name \"" + courseName + "\": " + e.getMessage());
			return -1;
		}
	}

	public boolean isStudentEnrolled(int studentId, int courseId) {
		try {
			int count = jdbcTemplate.queryForObject(CHECK_STUDENTENROLLMENT_SQL, Integer.class, studentId, courseId);
			return count > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		} catch (DataAccessException e) {
			System.err.println("Error checking student enrollment: " + e.getMessage());
			throw new RuntimeException("Failed to check student enrollment", e);
		}
	}

	public List<Course> getCoursesByStudentId(int studentId) {
		try {
			return jdbcTemplate.query(SELECT_COURSE_BY_STUDENT_ID_SQL, courseRowMapper, studentId);
		} catch (DataAccessException e) {
			System.err.println("Error retrieving courses for student with ID " + studentId + ": " + e.getMessage());
			throw new RuntimeException("Failed to retrieve courses for the student", e);
		}
	}

	@Transactional
	public void assignCourse(int studentId, int courseId) {
		jdbcTemplate.update(ASSIGN_COURSE_SQL, studentId, courseId);
	}

}
