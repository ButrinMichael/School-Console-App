package ua.SchoolConsoleApp.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.SchoolConsoleApp.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseDAO implements Dao<Course> {
	private final JdbcTemplate jdbcTemplate;

	private static final String InsertCourseSQL = "INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)";
	private static final String SelectCoursByIdSQL = "SELECT * FROM school.courses WHERE course_id = ?";
	private static final String UpdateCourseSQL = "UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?";;
	private static final String DeleteStudentCourseSQL = "DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?";;
	private static final String DeleteCourseSQL = "DELETE FROM School.COURSES WHERE course_id = ?";;
	private static final String SelectAllCoursesSQL = "SELECT * FROM school.COURSES";
	private static final String SelectCourseIdByNameSQL = "SELECT course_id FROM school.COURSES WHERE course_name = ?";
	private static final String CheckStudentEnrollmentSQL = "SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?";
	private static final String SelectCourseByStudentIdSQL = "SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
			+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";;
	private static final String AssignCourseSQL = "INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)";

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
	public void create(Course course) throws SQLException {
		jdbcTemplate.update(InsertCourseSQL, course.getName(), course.getDescription());
	}

	@Override
	public Optional<Course> read(int id) throws SQLException {
		return jdbcTemplate.query(SelectCoursByIdSQL, courseRowMapper, id).stream().findFirst();
	}

	@Override
	public void update(Course course) throws SQLException {
		jdbcTemplate.update(UpdateCourseSQL, course.getName(), course.getDescription(), course.getId());
	}

	@Override
	@Transactional
	public void delete(int id) throws SQLException {
		jdbcTemplate.update(DeleteStudentCourseSQL, id);
		jdbcTemplate.update(DeleteCourseSQL, id);
	}

	@Override
	public List<Course> getAll() {
		try {
			return jdbcTemplate.query(SelectAllCoursesSQL, courseRowMapper);
		} catch (DataAccessException e) {
			System.err.println("Error fetching all courses: " + e.getMessage());
			throw new RuntimeException("Failed to fetch courses", e);
		}
	}

	public int getCourseIdByName(String courseName) {
		try {
			return jdbcTemplate.queryForObject(SelectCourseIdByNameSQL, Integer.class, courseName);
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
			int count = jdbcTemplate.queryForObject(CheckStudentEnrollmentSQL, Integer.class, studentId, courseId);
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
			return jdbcTemplate.query(SelectCourseByStudentIdSQL, courseRowMapper, studentId);
		} catch (DataAccessException e) {
			System.err.println("Error retrieving courses for student with ID " + studentId + ": " + e.getMessage());
			throw new RuntimeException("Failed to retrieve courses for the student", e);
		}
	}

	@Transactional
	public void assignCourse(int studentId, int courseId) throws SQLException {
		jdbcTemplate.update(AssignCourseSQL, studentId, courseId);
	}
}
