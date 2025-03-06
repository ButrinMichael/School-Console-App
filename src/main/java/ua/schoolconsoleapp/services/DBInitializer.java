package ua.schoolconsoleapp.services;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(DBInitializer.class);
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
		logger.info("Starting database initialization...");
		try {
			createTables();
			insertGroupInitialData();
			insertCourseInitialData();
			insertStudentInitialData();
			insertStudentCoursesInitialData();
			logger.info("Database initialization completed successfully.");
		} catch (Exception e) {
			System.err.println("Database initialization error: " + e.getMessage());
			logger.error("Database initialization error: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void createTables() {
		String dbName = "DB.sql";
		logger.info("Executing SQL file: {}", dbName);
		dbFileReader.executeSQLFile(dbName);
		logger.info("Tables created successfully.");
	}

	private void insertGroupInitialData() {
		logger.info("Starting initial group data insertion...");
		List<String> groupNames = GroupNameGenerator.generateGroupNames(10);
		int id = 1;
		for (String groupName : groupNames) {
			Group group = new Group(id++, groupName);
			groupDAO.create(group);
			 logger.debug("Inserted group: {} (ID={})", groupName, group.getId());
		}
		 logger.info("Inserted {} groups into the database.", groupNames.size());
	}

	private void insertCourseInitialData() {
		logger.info("Starting initial course data insertion...");
		List<String> courseNames = CourseList.CourseNames();
		int id = 1;
		for (String courseName : courseNames) {
			Course course = new Course(id++, courseName, "Description");
			courseDAO.create(course);
			logger.debug("Inserted course: {} (ID={})", courseName, course.getId());
        }
        logger.info("Inserted {} courses into the database.", courseNames.size());
	}

	private void insertStudentInitialData() throws SQLException {
		 logger.info("Starting initial student data insertion...");
		List<String> studentsNames = StudentFirstLastNameGenerator.generateStudents(200);
		GroupIdGenerator groupIdGenerator = new GroupIdGenerator();
		for (String studentName : studentsNames) {
			Integer groupId = groupIdGenerator.generateGroupId();
			String[] parts = studentName.split(" ");
			String firstName = parts[0];
			String lastName = parts[1];
			Student student = new Student(0, groupId, firstName, lastName);
			studentsDAO.create(student);
			logger.debug("Inserted student: {} {} (GroupID={})", firstName, lastName, groupId);
        }
        logger.info("Inserted {} students into the database.", studentsNames.size());
			}

	private void insertStudentCoursesInitialData() {
		logger.info("Starting initial student-course assignments...");
		List<Student> students = studentsDAO.getAll();
		List<Course> courses = courseDAO.getAll();
		Random random = new Random();
		for (Student student : students) {
			int numCourses = random.nextInt(3) + 1;
			Collections.shuffle(courses);
			List<Course> selectedCourses = courses.subList(0, numCourses);
			for (Course course : selectedCourses) {
				courseDAO.assignCourse(student.getId(), course.getId());
				logger.debug("Assigned course '{}' (ID={}) to student {} {} (ID={})",
                        course.getName(), course.getId(), student.getFirstName(), student.getLastName(), student.getId());
            }
		}
		  logger.info("Student-course assignments completed.");
	}
}
