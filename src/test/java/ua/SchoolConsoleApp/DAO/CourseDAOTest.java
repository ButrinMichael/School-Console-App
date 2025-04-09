package ua.SchoolConsoleApp.DAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ua.schoolconsoleapp.dao.CourseDAOld;
import ua.schoolconsoleapp.models.Course;

@ExtendWith(MockitoExtension.class)
public class CourseDAOTest {
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

	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private RowMapper<Course> courseRowMapper;

	@InjectMocks
	private CourseDAOld courseDAOld;
	private Course testCourse;

	@BeforeEach
	public void setup() {
		testCourse = new Course(1, "Test Name", "Test Description");
	}

	@Test
    public void create_schouldCreateCourse() {
            when(jdbcTemplate.update(anyString(),anyString(),anyString())).thenReturn(1);
        			courseDAOld.create(testCourse);	      
          verify(jdbcTemplate, times(1)).update(
        		    eq(INSERT_COURSE_SQL),
        		    eq(testCourse.getName()),
        		    eq(testCourse.getDescription())
        		);
    }

	@Test
	public void create_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.update(anyString(), anyString(),anyString()))
	            .thenThrow(new DataAccessException("Database error") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	courseDAOld.create(testCourse);	
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq(INSERT_COURSE_SQL), eq(testCourse.getName()),eq(testCourse.getDescription()));
	}

	@Test
	public void read_shouldReturnResult_whenCourseFound() {    
	    when(jdbcTemplate.query(eq(SELECT_COURS_BY_ID_SQL), any(RowMapper.class), eq(1)))
	            .thenReturn(Collections.singletonList(testCourse));
	    Optional<Course> result = courseDAOld.read(1);
	    assertTrue(result.isPresent(), "Expected a course to be present in the Optional");
	    assertEquals(testCourse, result.get(), "The course returned does not match the expected course");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_COURS_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
	            .thenThrow(new DataAccessException("Failed to read course") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	courseDAOld.read(1);
	    });
	    assertEquals("Failed to read course", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_COURS_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnEmptyCourse_whenCourseNotFound() {
	      when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Course> result = courseDAOld.read(1);
	    assertFalse(result.isPresent(), "Expected no course to be present in the Optional when none is found");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_COURS_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void update_schouldUpdateCourse() {
		courseDAOld.update(testCourse);
		verify(jdbcTemplate, times(1)).update(UPDATE_COURSE_SQL, testCourse.getName(), testCourse.getDescription(),
				testCourse.getId());
	}

	@Test
	public void update_schouldThrowNullPointerException_whenCourseisNull() {
		assertThrows(NullPointerException.class, () -> {
			courseDAOld.update(null);
		});
	}

	@Test
	public void update_shouldReturnDataAccessException() {
	    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyInt())).thenThrow(new DataAccessException("Database error") {});
	    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	courseDAOld.update(testCourse);
	    });	
	    
	    assertEquals("Database error",  exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq(UPDATE_COURSE_SQL),eq(testCourse.getName()),eq (testCourse.getDescription()), eq(testCourse.getId()));
		}

	@Test
	public void delete_schouldPerformInOrder() {
		courseDAOld.delete(1);
		InOrder inOrder = inOrder(jdbcTemplate);
		inOrder.verify(jdbcTemplate).update(DELETE_STUDENT_COURSE_SQL, 1);
		inOrder.verify(jdbcTemplate).update(DELETE_COURSE_SQL, 1);
	}

	@Test
	public void delete_shouldDeleteCourse_WithPositiveId() {
		courseDAOld.delete(1);
		verify(jdbcTemplate).update(DELETE_STUDENT_COURSE_SQL, 1);
		verify(jdbcTemplate).update(DELETE_COURSE_SQL, 1);
	}

	@Test
	public void delete_shouldDeleteCourse_WithZeroId() {
		courseDAOld.delete(0);
		verify(jdbcTemplate).update(DELETE_STUDENT_COURSE_SQL, 0);
		verify(jdbcTemplate).update(DELETE_COURSE_SQL, 0);
	}

	@Test
	public void delete_shouldDeleteCourse_WithNegativeId() {
		courseDAOld.delete(-1);
		verify(jdbcTemplate).update(DELETE_STUDENT_COURSE_SQL, -1);
		verify(jdbcTemplate).update(DELETE_COURSE_SQL, -1);
	}

	@Test
    public void delete_shouldThrowDataAccessException_WhenDeletingFromStudentsCourses() {
        when(jdbcTemplate.update(eq(DELETE_STUDENT_COURSE_SQL), eq(1)))
                .thenThrow(new DataAccessException("Database error while deleting from STUDENTS_COURSES") {});

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            courseDAOld.delete(1);
        });
        assertEquals("Database error while deleting from STUDENTS_COURSES", exception.getMessage());
        verify(jdbcTemplate, times(0)).update(DELETE_COURSE_SQL, 1);
    }

	@Test
    public void delete_shouldThrowDataAccessException_WhenDeletingFromCourses() {
        when(jdbcTemplate.update(eq(DELETE_STUDENT_COURSE_SQL), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(eq(DELETE_COURSE_SQL), eq(1)))
                .thenThrow(new DataAccessException("Database error while deleting from COURSES") {});
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            courseDAOld.delete(1);
        });
        assertEquals("Database error while deleting from COURSES", exception.getMessage());
        verify(jdbcTemplate, times(1)).update(DELETE_STUDENT_COURSE_SQL, 1);
        verify(jdbcTemplate, times(1)).update(DELETE_COURSE_SQL, 1);
    }

	@Test
	public void getAll_schouldReturnCourseList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testCourse));
	    List<Course> result = courseDAOld.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test Name", result.get(0).getName());
	    assertEquals("Test Description", result.get(0).getDescription());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_ALL_COURSES_SQL), any(RowMapper.class));
	}

	@Test
	public void getAll_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
	            .thenThrow(new DataAccessException("Failed to fetch courses") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	courseDAOld.getAll();
	    });
	    assertEquals("Failed to fetch courses", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_ALL_COURSES_SQL), any(RowMapper.class));
	}

	@Test
	public void getCourseIdByName_shouldReturnCourseId_whenCourseExists() {
		
		when(jdbcTemplate.queryForObject(eq(SELECT_COURSE_ID_BY_NAME_SQL),
				eq(Integer.class), eq(testCourse.getName()))).thenReturn(testCourse.getId());
		int result = courseDAOld.getCourseIdByName(testCourse.getName());
		assertEquals(testCourse.getId(), result);
		verify(jdbcTemplate, times(1)).queryForObject(eq(SELECT_COURSE_ID_BY_NAME_SQL),
				eq(Integer.class), eq(testCourse.getName()));
	}

	@Test
	public void getCourseIdByName_shouldReturnMinusOne_whenCourseNotFound() {	    
	     when(jdbcTemplate.queryForObject(
	            eq(SELECT_COURSE_ID_BY_NAME_SQL),eq(Integer.class),eq(testCourse.getName())))
	        .thenThrow(new EmptyResultDataAccessException(1));
	    int result = courseDAOld.getCourseIdByName(testCourse.getName());
	    assertEquals(-1, result);
	    verify(jdbcTemplate, times(1)).queryForObject(
	            eq(SELECT_COURSE_ID_BY_NAME_SQL),eq(Integer.class),eq(testCourse.getName()));
	}

	@Test
	public void getCourseIdByName_shouldReturnMinusOne_whenDataAccessExceptionOccurs() {
	    when(jdbcTemplate.queryForObject(
	            eq(SELECT_COURSE_ID_BY_NAME_SQL),eq(Integer.class),eq(testCourse.getName())))
	        .thenThrow(new DataAccessException("Database error while deleting from COURSES") {});
	int result = courseDAOld.getCourseIdByName(testCourse.getName());
    assertEquals(-1, result);
    verify(jdbcTemplate, times(1)).queryForObject(
            eq(SELECT_COURSE_ID_BY_NAME_SQL),eq(Integer.class),eq(testCourse.getName()));
	
	}

	@Test
	public void isStudentEnrolled_shouldReturnTrue_whenStudentIsEnrolled() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()))).thenReturn(1);
		boolean result = courseDAOld.isStudentEnrolled(studentId, testCourse.getId());
		assertTrue(result);
		verify(jdbcTemplate, times(1)).queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldReturnFalse_whenStudentIsNotEnrolled() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()))).thenReturn(0);
		boolean result = courseDAOld.isStudentEnrolled(studentId, testCourse.getId());
		assertFalse(result);
		verify(jdbcTemplate, times(1)).queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldReturnFalse_whenEmptyResultDataAccessExceptionThrown() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()))).thenThrow(new EmptyResultDataAccessException(1));
		boolean result = courseDAOld.isStudentEnrolled(studentId, testCourse.getId());
		assertFalse(result);
		verify(jdbcTemplate, times(1)).queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldThrowDataAccessException_whenDataAccessExceptionThrown() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()))).thenThrow(new DataAccessException("Database error") {
				});
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			courseDAOld.isStudentEnrolled(studentId, testCourse.getId());
		});
		assertEquals("Failed to check student enrollment", exception.getMessage());
		verify(jdbcTemplate, times(1)).queryForObject(eq(CHECK_STUDENTENROLLMENT_SQL), eq(Integer.class), eq(studentId),
				eq(testCourse.getId()));
	}

	@Test
	public void getCoursesByStudentId_shouldReturnCourseList_whenCoursesExist() {

		List<Course> expectedCourses = Collections.singletonList(testCourse);
		when(jdbcTemplate.query(eq(SELECT_COURSE_BY_STUDENT_ID_SQL), any(RowMapper.class), eq(testCourse.getId())))
				.thenReturn(expectedCourses);
		List<Course> result = courseDAOld.getCoursesByStudentId(testCourse.getId());
		assertEquals(expectedCourses.size(), result.size());
		assertEquals(expectedCourses.get(0).getName(), result.get(0).getName());
		verify(jdbcTemplate, times(1)).query(eq(SELECT_COURSE_BY_STUDENT_ID_SQL), any(RowMapper.class),
				eq(testCourse.getId()));
	}

	@Test
	public void getCoursesByStudentId_shouldReturnEmptyList_whenNoCoursesExist() {
	    when(jdbcTemplate.query(
	            anyString(), 
	            any(RowMapper.class), 
	            eq(testCourse.getId())
	    )).thenReturn(Collections.emptyList());
	    List<Course> result = courseDAOld.getCoursesByStudentId(testCourse.getId());
	    assertTrue(result.isEmpty());
	    verify(jdbcTemplate, times(1)).query(
	            eq(SELECT_COURSE_BY_STUDENT_ID_SQL),
	            any(RowMapper.class), 
	            eq(testCourse.getId())
	    );
	}

	@Test
	public void getCoursesByStudentId_shouldThrowException_whenDataAccessExceptionOccurs() {
	    when(jdbcTemplate.query(
	            anyString(),
	            any(RowMapper.class),
	            eq(testCourse.getId())
	    )).thenThrow(new DataAccessException("Failed to retrieve courses") {});
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        courseDAOld.getCoursesByStudentId(testCourse.getId());
	    });
	    assertEquals("Failed to retrieve courses for the student", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(
	    		 eq(SELECT_COURSE_BY_STUDENT_ID_SQL),
	    		 any(RowMapper.class),
	            eq(testCourse.getId())
	    );
	}

	@Test
	public void assignCourse_shouldAssignCourseSuccessfully() {
		int studentId = 1;
		courseDAOld.assignCourse(studentId, testCourse.getId());
		verify(jdbcTemplate, times(1)).update(eq(ASSIGN_COURSE_SQL), eq(studentId), eq(testCourse.getId()));
	}

	@Test
	public void assignCourse_shouldThrowDataAccessException() {
		int studentId = 1;
		when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenThrow(new DataAccessException("Database error") {
		});

		assertThrows(DataAccessException.class, () -> {
			courseDAOld.assignCourse(studentId, testCourse.getId());
		});
		verify(jdbcTemplate, times(1)).update(eq(ASSIGN_COURSE_SQL), eq(studentId), eq(testCourse.getId()));
	}
}
