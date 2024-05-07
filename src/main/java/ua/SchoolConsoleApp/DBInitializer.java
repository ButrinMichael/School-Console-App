package ua.SchoolConsoleApp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ua.SchoolConsoleApp.DAO.CourseDAO;
import ua.SchoolConsoleApp.DAO.GroupDAO;
import ua.SchoolConsoleApp.DAO.StudentsCoursesDAO;
import ua.SchoolConsoleApp.DAO.StudentsDAO;
import ua.SchoolConsoleApp.DB.DB_file_reader;
import ua.SchoolConsoleApp.DB.DatabaseConnection;

public class DBInitializer {
	public static void initializeDatabase() {
		try (Connection connection = DatabaseConnection.getConnection()) {
			createTables(connection);
			insertGroupInitialData(connection);
			insertCourseInitialData(connection);
			insertStudentInitialData(connection);
			insertStudentCoursesInitialData(connection);

		} catch (SQLException e) {
			System.err.println("Database initialization error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void createTables(Connection connection) throws SQLException {
		String DBname = "DB.sql";
		DB_file_reader.executeSQLFile(DBname);
	}

	private static void insertGroupInitialData(Connection connection) throws SQLException {
		List<String> groupNames = GroupNameGenerator.generateGroupNames(10);
		try {
			GroupDAO groupDAO = new GroupDAO(connection);
			int id = 1;
			for (String groupName : groupNames) {
				Group group = new Group(id++, groupName);
				groupDAO.create(group);
			}
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void insertCourseInitialData(Connection connection) throws SQLException {
		List<String> courseNames = CourseList.CourseNames();
		try {
			CourseDAO courseDAO = new CourseDAO(connection);
			int id = 1;
			for (String courseName : courseNames) {
				Course course = new Course(id++, courseName, "Description");
				courseDAO.create(course);
			}
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void insertStudentInitialData(Connection connection) throws SQLException {
		List<String> studentsNames = StudentFirsLastNameGenerator.generateStudents(200);
		try {
			StudentsDAO studentsDao = new StudentsDAO(connection);
			GroupIdGenerator groupIdGenerator = new GroupIdGenerator();	
			for (String studentName : studentsNames) {
				Integer groupId = groupIdGenerator.generateGroupId();
				String[] parts = studentName.split(" ");
				String firstName = parts[0];
				String lastName = parts[1];
				Student student = new Student(0, groupId, firstName, lastName);
				studentsDao.create(student);
			}
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void insertStudentCoursesInitialData(Connection connection) throws SQLException {
		StudentsDAO studentDAO = new StudentsDAO(connection);
		CourseDAO courseDAO = new CourseDAO(connection);
		StudentsCoursesDAO studentsCoursesDAO = new StudentsCoursesDAO(connection);
		List<Student> students = studentDAO.getAll();
		List<Course> courses = courseDAO.getAll();
		Random random = new Random();
		for (Student student : students) {
			int numCourses = random.nextInt(3) + 1;
			Collections.shuffle(courses);
			List<Course> selectedCourses = courses.subList(0, numCourses);
			for (Course course : selectedCourses) {
				studentsCoursesDAO.assignCourse(student.getId(), course.getId());
			}
		}
	}

}
