package ua.SchoolConsoleApp.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;

import ua.SchoolConsoleApp.Course;
import ua.SchoolConsoleApp.Student;
        
public class StudentsDAO  implements Dao<Student> {
	private final Connection connection;

	public StudentsDAO(Connection connection) {
		this.connection = connection;
	}
   
	    
	@Override
	public Student read(int id) throws SQLException {
		Student student = null;
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM school.students WHERE student_id = ?");
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Integer groupId = resultSet.getInt("group_id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				student = new Student(id, groupId, firstName, lastName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return student;
	}

	@Override
	public void update(Student student) throws SQLException {
		String sql = "UPDATE school.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, student.getGroupId());
			statement.setString(2, student.getFirstName());
			statement.setString(3, student.getLastName());
			statement.setInt(4, student.getId());
			statement.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		try {
			PreparedStatement deleteCoursesStatement = connection
					.prepareStatement("DELETE FROM School.STUDENTS_COURSES WHERE student_id = ?");
			deleteCoursesStatement.setInt(1, id);
			deleteCoursesStatement.executeUpdate();
			PreparedStatement deleteStudentStatement = connection
					.prepareStatement("DELETE FROM School.STUDENTS WHERE student_id = ?");
			deleteStudentStatement.setInt(1, id);
			deleteStudentStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void create(Student student) throws SQLException {
		String sql = "INSERT INTO school.STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        if (student.getGroupId() != null) {
	            statement.setInt(1, student.getGroupId());
	        } else {
	            statement.setNull(1, Types.INTEGER);
	        }
	        statement.setString(2, student.getFirstName());
	        statement.setString(3, student.getLastName());
	        statement.executeUpdate();
	    }
	}

	@Override
	public List<Student> getAll() {
		List<Student> students = new ArrayList<>();
		String sql = "SELECT * FROM school.STUDENTS";
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				int id = resultSet.getInt("student_id");
				int groupId = resultSet.getInt("group_id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				students.add(new Student(id, groupId, firstName, lastName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}

	public int getNumStudentsInGroup(int groupId) {
		int numStudents = 0;
		String sql = "SELECT COUNT(*) FROM school.students WHERE group_id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, groupId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				numStudents = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numStudents;
	}

	public List<Student> getStudentsByCourseName(String courseName) throws SQLException {
		List<Student> students = new ArrayList<>();
		String sql = "SELECT s.* FROM school.students s "
				+ "JOIN school.students_courses sc ON s.student_id = sc.student_id "
				+ "JOIN school.courses c ON sc.course_id = c.course_id " + "WHERE c.course_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, courseName);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt("student_id");
				int groupId = resultSet.getInt("group_id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				Student student = new Student(id, groupId, firstName, lastName);
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}

	public int getStudentIdByName(String firstName, String lastName) throws SQLException {
		int studentId = -1;
		String sql = "SELECT student_id FROM school.students WHERE first_name = ? AND last_name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				studentId = resultSet.getInt("student_id");
			}
		}

		return studentId;
	}
	
	public void removeStudentFromCourse(int studentId, int courseId) throws SQLException {
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
	
	public void removeCourseFromStudent(int studentId, int courseId) throws SQLException {
	    String sql = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, studentId);
	        statement.setInt(2, courseId);
	        int rowsAffected = statement.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Course successfully removed from the student!");
	        } else {
	            System.out.println("Failed to remove course from the student.");
	        }
	    }
	}
	
	public void addCourseToStudent(int studentId, int courseId) throws SQLException {
	    String sql = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, studentId);
	        statement.setInt(2, courseId);
	        int rowsAffected = statement.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Course successfully added to the student!");
	        } else {
	            System.out.println("Failed to add course to the student.");
	        }
	    }
	}
	
	public List<Course> getCoursesByStudentId(int studentId) throws SQLException {
	    List<Course> courses = new ArrayList<>();
	    String sql = "SELECT c.* FROM school.courses c " +
	                 "JOIN school.students_courses sc ON c.course_id = sc.course_id " +
	                 "WHERE sc.student_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, studentId);
	        ResultSet resultSet = statement.executeQuery();
	        while (resultSet.next()) {
	            int courseId = resultSet.getInt("course_id");
	            String courseName = resultSet.getString("course_name");
	            Course course = new Course(courseId, courseName);
	            courses.add(course);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return courses;
	}

	public List<Student> getStudentsByCourseId(int courseId) throws SQLException {
	    List<Student> students = new ArrayList<>();
	    String sql = "SELECT s.* FROM school.students s " +
	                 "JOIN school.students_courses sc ON s.student_id = sc.student_id " +
	                 "WHERE sc.course_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, courseId);
	        ResultSet resultSet = statement.executeQuery();
	        while (resultSet.next()) {
	            int studentId = resultSet.getInt("student_id");
	            int groupId = resultSet.getInt("group_id");
	            String firstName = resultSet.getString("first_name");
	            String lastName = resultSet.getString("last_name");
	            Student student = new Student(studentId, groupId, firstName, lastName);
	            students.add(student);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return students;
	}

}