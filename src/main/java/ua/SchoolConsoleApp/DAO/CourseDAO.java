package ua.SchoolConsoleApp.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ua.SchoolConsoleApp.Course;

public class CourseDAO implements Dao<Course> {
	private final Connection connection;

	public CourseDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(Course course) throws SQLException {
		String sql = "INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, course.getName());
			statement.setString(2, course.getDescription());
			statement.executeUpdate();
		}
	}

	@Override
	public Course read(int id) throws SQLException {
		Course course = null;
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM school.courses WHERE course_id = ?");
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String name = resultSet.getString("course_name");
				String description = resultSet.getString("course_description");
				course = new Course(id, name, description);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return course;
	}

	@Override
	public void delete(int id) throws SQLException {
		try {
			PreparedStatement deleteCoursesStatement = connection
					.prepareStatement("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?");
			deleteCoursesStatement.setInt(1, id);
			deleteCoursesStatement.executeUpdate();

			PreparedStatement deleteCourseStatement = connection
					.prepareStatement("DELETE FROM School.COURSES WHERE course_id = ?");
			deleteCourseStatement.setInt(1, id);
			deleteCourseStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(Course course) throws SQLException {
		String sql = "UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, course.getName());
			statement.setString(2, course.getDescription());
			statement.setInt(3, course.getId());
			statement.executeUpdate();
		}
	}

	@Override
	public List<Course> getAll() {
		List<Course> courses = new ArrayList<>();
		String sql = "SELECT * FROM school.COURSES";
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				int id = resultSet.getInt("course_id");
				String name = resultSet.getString("course_name");
				String description = resultSet.getString("course_description");
				courses.add(new Course(id, name, description));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	public int getCourseIdByName(String courseName) throws SQLException {
		int courseId = -1;
		String sql = "SELECT course_id FROM school.COURSES WHERE course_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, courseName);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				courseId = resultSet.getInt("course_id");
			}
		}

		return courseId;
	}

	public boolean isStudentEnrolled(int studentId, int courseId) throws SQLException {
		boolean enrolled = false;
		String sql = "SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, studentId);
			statement.setInt(2, courseId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				enrolled = (count > 0);
			}
		}

		return enrolled;
	}

	public List<Course> getCoursesByStudentId(int studentId) throws SQLException {
		List<Course> courses = new ArrayList<>();
		String sql = "SELECT c.course_id, c.course_name FROM school.courses c "
				+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, studentId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int courseId = resultSet.getInt("course_id");
				String courseName = resultSet.getString("course_name");
				courses.add(new Course(courseId, courseName));
			}
		}
		return courses;
	}
	
	public void assignCourse(int studentId, int courseId) {
		String sql = "INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, studentId);
			statement.setInt(2, courseId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}