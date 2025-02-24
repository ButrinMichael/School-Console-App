package ua.schoolconsoleapp.services;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.schoolconsoleapp.dao.CourseDAO;
import ua.schoolconsoleapp.dao.GroupDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.db.DBFileReader;
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.utils.CourseList;
import ua.schoolconsoleapp.utils.GroupIdGenerator;
import ua.schoolconsoleapp.utils.GroupNameGenerator;
import ua.schoolconsoleapp.utils.StudentFirstLastNameGenerator;

@Component
public class DBInitializer {

	private final DBFileReader dbFileReader;
	private final GroupDAO groupDAO;
	private final CourseDAO courseDAO;
	private final StudentsDAO studentsDAO;

	@Autowired
	public DBInitializer(DBFileReader dbFileReader, GroupDAO groupDAO, CourseDAO courseDAO, StudentsDAO studentsDAO) {
		this.dbFileReader = dbFileReader;
		this.groupDAO = groupDAO;
		this.courseDAO = courseDAO;
		this.studentsDAO = studentsDAO;
	}

	public void initializeDatabase() {
		try {
			createTables();
			insertGroupInitialData();
			insertCourseInitialData();
			insertStudentInitialData();
			insertStudentCoursesInitialData();
		} catch (Exception e) {
			System.err.println("Database initialization error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void createTables() {
		String dbName = "DB.sql";
		dbFileReader.executeSQLFile(dbName);
	}

	private void insertGroupInitialData() {
		List<String> groupNames = GroupNameGenerator.generateGroupNames(10);
		int id = 1;
		for (String groupName : groupNames) {
			Group group = new Group(id++, groupName);
			groupDAO.create(group);
		}
	}

	private void insertCourseInitialData() {
		List<String> courseNames = CourseList.CourseNames();
		int id = 1;
		for (String courseName : courseNames) {
			Course course = new Course(id++, courseName, "Description");
			courseDAO.create(course);
		}
	}

	private void insertStudentInitialData() throws SQLException {
		List<String> studentsNames = StudentFirstLastNameGenerator.generateStudents(200);
		GroupIdGenerator groupIdGenerator = new GroupIdGenerator();
		for (String studentName : studentsNames) {
			Integer groupId = groupIdGenerator.generateGroupId();
			String[] parts = studentName.split(" ");
			String firstName = parts[0];
			String lastName = parts[1];
			Student student = new Student(0, groupId, firstName, lastName);
			studentsDAO.create(student);
		}
	}

	private void insertStudentCoursesInitialData() {
		List<Student> students = studentsDAO.getAll();
		List<Course> courses = courseDAO.getAll();
		Random random = new Random();
		for (Student student : students) {
			int numCourses = random.nextInt(3) + 1;
			Collections.shuffle(courses);
			List<Course> selectedCourses = courses.subList(0, numCourses);
			for (Course course : selectedCourses) {
				courseDAO.assignCourse(student.getId(), course.getId());
			}
		}
	}
}
