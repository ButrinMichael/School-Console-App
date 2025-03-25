package ua.schoolconsoleapp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.schoolconsoleapp.models.Course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseDAOld implements Dao<Course> {
	private static final Logger logger = LoggerFactory.getLogger(CourseDAOld.class);
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_COURSE_SQL = "INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)";
	private static final String SELECT_COURS_BY_ID_SQL = "SELECT * FROM school.courses WHERE course_id = ?";
	private static final String UPDATE_COURSE_SQL = "UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?";
	private static final String DELETE_STUDENT_COURSE_SQL = "DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?";
	private static final String DELETE_COURSE_SQL = "DELETE FROM School.COURSES WHERE course_id = ?";
	private static final String SELECT_ALL_COURSES_SQL = "SELECT * FROM school.COURSES";
	private static final String SELECT_COURSE_ID_BY_NAME_SQL = "SELECT course_id FROM school.COURSES WHERE course_name = ?";
	private static final String CHECK_STUDENTENROLLMENT_SQL = "SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?";
	private static final String SELECT_COURSE_BY_STUDENT_ID_SQL = "SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
			+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
	private static final String ASSIGN_COURSE_SQL = "INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)";

	@Autowired
	public CourseDAOld(JdbcTemplate jdbcTemplate) {
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
	public void create(Course course) {
        logger.info("Creating course: {}", course.getName());
        try {
            jdbcTemplate.update(INSERT_COURSE_SQL, course.getName(), course.getDescription());
            logger.info("Course '{}' created successfully.", course.getName());
        } catch (DataAccessException e) {
            logger.error("Failed to create course '{}': {}", course.getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to create course", e);
        }
    }

	public Optional<Course> read(int id) {
        logger.info("Reading course with ID: {}", id);
        try {
            Optional<Course> course = jdbcTemplate.query(SELECT_COURS_BY_ID_SQL, courseRowMapper, id).stream().findFirst();
            if (course.isPresent()) {
                logger.info("Course found: {}", course.get());
            } else {
                logger.warn("Course with ID {} not found.", id);
            }
            return course;
        } catch (DataAccessException e) {
            logger.error("Failed to read course with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to read course", e);
        }
    }
	@Override
    public void update(Course course) {
        logger.info("Updating course with ID: {} to new name '{}' and description '{}'",
                course.getId(), course.getName(), course.getDescription());
        try {
            jdbcTemplate.update(UPDATE_COURSE_SQL, course.getName(), course.getDescription(), course.getId());
            logger.info("Course with ID {} updated successfully.", course.getId());
        } catch (DataAccessException e) {
            logger.error("Failed to update course with ID {}: {}", course.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update course", e);
        }
    }

	@Override
	@Transactional
	public void delete(int id) {
        logger.info("Deleting course with ID: {}", id);
        try {
            jdbcTemplate.update(DELETE_STUDENT_COURSE_SQL, id);  
            jdbcTemplate.update(DELETE_COURSE_SQL, id);          
            logger.info("Course with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            logger.error("Failed to delete course with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete course", e);
        }
    }

	public List<Course> getAll() {
        logger.info("Fetching all courses");
        try {
            List<Course> courses = jdbcTemplate.query(SELECT_ALL_COURSES_SQL, courseRowMapper);
            logger.info("Fetched {} courses from database.", courses.size());
            return courses;
        } catch (DataAccessException e) {
            logger.error("Failed to fetch all courses: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch courses", e);
        }
    }

	
	public int getCourseIdByName(String courseName) {
		 logger.info("Fetching course ID for course name: {}", courseName);
		try {		
			Integer courseId = jdbcTemplate.queryForObject(SELECT_COURSE_ID_BY_NAME_SQL, Integer.class, courseName);
            if (courseId != null) {
                logger.info("Course ID for '{}' is {}", courseName, courseId);
                return courseId;
            } else {
                logger.warn("Course '{}' not found.", courseName);
                return -1;
            }
		} catch (DataAccessException e) {
			logger.error("Failed to fetch course ID for '{}': {}", courseName, e.getMessage(), e);
			System.err.println("Failed to retrieve course ID for name \"" + courseName + "\": " + e.getMessage());
			return -1;
		}
	}

	public boolean isStudentEnrolled(int studentId, int courseId) {
		logger.info("Checking if student with ID {} is enrolled in course with ID {}", studentId, courseId);
		try {
			int count = jdbcTemplate.queryForObject(CHECK_STUDENTENROLLMENT_SQL, Integer.class, studentId, courseId);
			logger.debug("Student with ID {} enrollment status in course with ID {}: {}", studentId, courseId,count);
			return count > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		} catch (DataAccessException e) {
			logger.error("Failed to check enrollment for student with ID {} in course with ID {}: {}", studentId, courseId, e.getMessage(), e);
			System.err.println("Error checking student enrollment: " + e.getMessage());
			throw new RuntimeException("Failed to check student enrollment", e);
		}
	}

	public List<Course> getCoursesByStudentId(int studentId) {
		logger.info("Fetching courses for student with ID: {}", studentId);
	    try {
	        List<Course> courses = jdbcTemplate.query(SELECT_COURSE_BY_STUDENT_ID_SQL, courseRowMapper, studentId);
	        logger.info("Fetched {} courses for student with ID: {}", courses.size(), studentId);
	        return courses;
		} catch (DataAccessException e) {
			logger.error("Error retrieving courses for student with ID {}: {}", studentId, e.getMessage(), e);
			System.err.println("Error retrieving courses for student with ID " + studentId + ": " + e.getMessage());
			throw new RuntimeException("Failed to retrieve courses for the student", e);
		}
	}

	@Transactional
	public void assignCourse(int studentId, int courseId) {
        logger.info("Assigning student with ID {} to course with ID {}", studentId, courseId);
        try {
            jdbcTemplate.update(ASSIGN_COURSE_SQL, studentId, courseId);
            logger.info("Student with ID {} successfully assigned to course with ID {}", studentId, courseId);
        } catch (DataAccessException e) {
            logger.error("Failed to assign student with ID {} to course with ID {}: {}", studentId, courseId, e.getMessage(), e);
            throw new RuntimeException("Failed to assign student to course", e);
        }
    }

}
