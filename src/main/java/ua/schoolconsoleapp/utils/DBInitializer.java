package ua.schoolconsoleapp.utils;


import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import ua.schoolconsoleapp.dao.JPACourseDAO;
import ua.schoolconsoleapp.dao.JPAGroupDAO;
import ua.schoolconsoleapp.dao.JPAStudentDAO;
import ua.schoolconsoleapp.db.DBFileReader;
import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.models.Student;

@Component
public class DBInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(DBInitializer.class);
	
	private final DBFileReader dbFileReader;
	private final JPAGroupDAO jpaGroupDAO;
	private final JPACourseDAO jpaCourseDAO;
	private final JPAStudentDAO jpaStudentDAO;

	@Autowired
	public DBInitializer(DBFileReader dbFileReader, JPAGroupDAO jpaGroupDAO, JPACourseDAO jpaCourseDAO,
			JPAStudentDAO jpaStudentDAO) {
		this.dbFileReader = dbFileReader;
		this.jpaGroupDAO = jpaGroupDAO;
		this.jpaCourseDAO = jpaCourseDAO;
		this.jpaStudentDAO = jpaStudentDAO;
	}

	public void initializeDatabase() {
		logger.info("Starting database initialization...");
		try {
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

	@Transactional
	public void insertGroupInitialData() {
		logger.info("Starting initial group data insertion...");
		List<String> groupNames = GroupNameGenerator.generateGroupNames(10);

		for (String groupName : groupNames) {
			Group group = new Group();
			group.setName(groupName);
			jpaGroupDAO.create(group);
			logger.debug("Inserted group: {}", group.getName());
		}

		logger.info("Inserted {} groups into the database.", groupNames.size());
	}

	@Transactional
	public void insertCourseInitialData() {
		logger.info("Starting initial course data insertion...");
		List<String> courseNames = CourseList.CourseNames();

		for (String courseName : courseNames) {
			Course course = new Course();
			course.setName(courseName);
			course.setDescription("Description");
			jpaCourseDAO.create(course);
			logger.debug("Inserted course: {}", course.getName());
		}

		logger.info("Inserted {} courses into the database.", courseNames.size());
	}

	@Transactional
	public void insertStudentInitialData() {
		logger.info("Starting initial student data insertion...");
		List<String> studentNames = StudentFirstLastNameGenerator.generateStudents(200);
		List<Group> existingGroups = jpaGroupDAO.getAll();

		Random random = new Random();
		int insertedCount = 0;

		for (String studentName : studentNames) {
			String[] parts = studentName.split(" ");
			if (parts.length != 2) {
				logger.warn("Invalid student name format: {}", studentName);
				continue;
			}

			String firstName = parts[0];
			String lastName = parts[1];

			Group group = null;

			if (!existingGroups.isEmpty() && random.nextDouble() > 0.1) {
				group = existingGroups.get(random.nextInt(existingGroups.size()));
			}

			Student student = new Student(group, firstName, lastName);
			jpaStudentDAO.create(student);
			insertedCount++;

			logger.debug("Inserted student: {} {} (GroupID={})", firstName, lastName,
					group != null ? group.getId() : "null");
		}

		logger.info("Inserted {} students into the database.", insertedCount);
	}

	@Transactional
	public void insertStudentCoursesInitialData() {
		logger.info("Starting initial student-course assignments...");
		List<Student> students = jpaStudentDAO.getAllWithCourses();
		List<Course> courses = jpaCourseDAO.getAll();
		Random random = new Random();

		if (courses.isEmpty()) {
			logger.warn("No courses available for assignment. Skipping student-course assignment.");
			return;
		}

		int totalAssignments = 0;

		for (Student student : students) {
			int numCourses = random.nextInt(3) + 1; 
			Collections.shuffle(courses);
			List<Course> selectedCourses = courses.subList(0, Math.min(numCourses, courses.size()));

			for (Course course : selectedCourses) {
				student.getCourses().add(course);
				logger.debug("Assigned course '{}' (ID={}) to student {} {} (ID={})", course.getName(), course.getId(),
						student.getFirstName(), student.getLastName(), student.getId());
				totalAssignments++;
			}

			jpaStudentDAO.update(student);
		}

		logger.info("Student-course assignments completed. Total assignments: {}", totalAssignments);
	}
}
