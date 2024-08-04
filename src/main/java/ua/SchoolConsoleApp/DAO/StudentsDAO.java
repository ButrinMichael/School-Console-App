package ua.SchoolConsoleApp.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ua.SchoolConsoleApp.Student;
import ua.SchoolConsoleApp.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentsDAO implements Dao<Student> {
	private final JdbcTemplate jdbcTemplate;

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
		String sql = "INSERT INTO school.STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, student.getGroupId(), student.getFirstName(), student.getLastName());
	}

	@Override
	public void update(Student student) {
		String sql = "UPDATE school.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";
		jdbcTemplate.update(sql, student.getGroupId(), student.getFirstName(), student.getLastName(), student.getId());
	}

	@Override
	public void delete(int id) {
		String deleteCoursesSql = "DELETE FROM School.STUDENTS_COURSES WHERE student_id = ?";
		jdbcTemplate.update(deleteCoursesSql , id);
		
		String deleteStudentSql  = "DELETE FROM school.students WHERE student_id = ?";
		jdbcTemplate.update(deleteStudentSql , id);
	}


	@Override
	public Student read(int id) {
		String sql = "SELECT * FROM school.students WHERE student_id = ?";
		List<Student> students = jdbcTemplate.query(sql, studentRowMapper, id);
		if (students.isEmpty()) {
			return null;
		} else {
			return students.get(0);
		}
	}

	@Override
	public List<Student> getAll() {
		String sql = "SELECT * FROM school.students";
		return jdbcTemplate.query(sql, studentRowMapper);
	}

	public int getNumStudentsInGroup(int groupId) {
		String sql = "SELECT COUNT(*) FROM school.students WHERE group_id = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, groupId);
	}

	public List<Student> getStudentsByCourseName(String courseName) {
		String sql = "SELECT s.* FROM school.students s "
				+ "JOIN school.students_courses sc ON s.student_id = sc.student_id "
				+ "JOIN school.courses c ON sc.course_id = c.course_id " + "WHERE c.course_name = ?";
		return jdbcTemplate.query(sql, studentRowMapper, courseName);

	}

	public int getStudentIdByName(String firstName, String lastName) {
		String sql = "SELECT student_id FROM school.students WHERE first_name = ? AND last_name = ? AND (group_id IS NULL OR group_id IS NOT NULL)";
	    try {
	        return jdbcTemplate.queryForObject(sql, Integer.class, firstName, lastName);
	    } catch (EmptyResultDataAccessException e) {
	        return -1;
	    }
	}


	public void removeStudentFromCourse(int studentId, int courseId) {
		String sql = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
		int rowsAffected = jdbcTemplate.update(sql, studentId, courseId);
		if (rowsAffected > 0) {
			System.out.println("Student successfully removed from the course!");
		} else {
			System.out.println("Failed to remove student from the course.");
		}
	}

	public void removeCourseFromStudent(int studentId, int courseId) {
		String sql = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
		int rowsAffected = jdbcTemplate.update(sql, studentId, courseId);
		if (rowsAffected > 0) {
			System.out.println("Course successfully removed from the student!");
		} else {
			System.out.println("Failed to remove course from the student.");
		}
	}

	public int addCourseToStudent(int studentId, int courseId) {
		String sql = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
		return jdbcTemplate.update(sql, studentId, courseId);	
	}

	public List<Course> getCoursesByStudentId(int studentId) {
		String sql = "SELECT c.* FROM school.courses c "
				+ "JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
		return jdbcTemplate.query(sql, courseRowMapper, studentId);
	}

	public List<Student> getStudentsByCourseId(int courseId) {
		String sql = "SELECT s.* FROM school.students s "
				+ "JOIN school.students_courses sc ON s.student_id = sc.student_id " + "WHERE sc.course_id = ?";
		return jdbcTemplate.query(sql, studentRowMapper, courseId);
	}
}
