package ua.SchoolConsoleApp.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ua.SchoolConsoleApp.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CourseDAO implements Dao<Course> {
	private final JdbcTemplate jdbcTemplate;

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
		String sql = "INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)";
		jdbcTemplate.update(sql, course.getName(), course.getDescription());
	}

	@Override
	public Course read(int id) throws SQLException {
		String sql = "SELECT * FROM school.courses WHERE course_id = ?";
		List<Course> courses = jdbcTemplate.query(sql, courseRowMapper, id);
		if (courses.isEmpty()) {
			return null;
		} else {
			return courses.get(0);
		}
	}

	@Override
	public void update(Course course) throws SQLException {
		String sql = "UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?";
		jdbcTemplate.update(sql, course.getName(), course.getDescription(), course.getId());
	}

	@Override
	public void delete(int id) throws SQLException {
		String deleteStudentCourseSql = "DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?";
		String deleteCourseSql = "DELETE FROM School.COURSES WHERE course_id = ?";
		jdbcTemplate.update(deleteStudentCourseSql, id);
		jdbcTemplate.update(deleteCourseSql, id);
	}

	@Override
	public List<Course> getAll() throws SQLException {
		String sql = "SELECT * FROM school.COURSES";
		return jdbcTemplate.query(sql, courseRowMapper);
	}

	public int getCourseIdByName(String courseName) throws SQLException {
		String sql = "SELECT course_id FROM school.COURSES WHERE course_name = ?";		
		try {
	        return jdbcTemplate.queryForObject(sql, Integer.class, courseName);
	    } catch (EmptyResultDataAccessException e) {
	        return -1;
	    }
	}

	public boolean isStudentEnrolled(int studentId, int courseId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, courseId);
		return count > 0;
	}

	public List<Course> getCoursesByStudentId(int studentId) throws SQLException {
		String sql = "SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
				+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
		return jdbcTemplate.query(sql, courseRowMapper, studentId);
	}

	public void assignCourse(int studentId, int courseId) throws SQLException {
		String sql = "INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)";
		jdbcTemplate.update(sql, studentId, courseId);
	}
}
