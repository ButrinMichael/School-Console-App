package ua.SchoolConsoleApp.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import ua.SchoolConsoleApp.StudentsCourses;

public class StudentsCoursesDAO implements Dao<StudentsCourses> {
	private final Connection connection;

	public StudentsCoursesDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(StudentsCourses studentsCourses) throws SQLException {
		String sql = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, studentsCourses.getStudID());
			statement.setInt(2, studentsCourses.getCourseID());
			statement.executeUpdate();
		}
	}

	@Override
	public void update(StudentsCourses entity) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public StudentsCourses read(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentsCourses> getAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int id) throws SQLException {
		// TODO Auto-generated method stub

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

	public void deleteStudentFromCourse(int studentId, int courseId) throws SQLException {
		String sql = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, studentId);
			statement.setInt(2, courseId);
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Student successfully removed from the course!");
			} else {
				System.out.println("Failed to remove student from the course.");
			}
		}
	}
}
