package ua.SchoolConsoleApp.DAO;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractDao<T> {
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	public AbstractDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public abstract void addCourseToStudent(int studentId, int courseId) throws SQLException;

	public abstract void removeCourseFromStudent(int studentId, int courseId) throws SQLException;

	public abstract void removeStudentFromCourse(int courseId, int studentId) throws SQLException;
}
