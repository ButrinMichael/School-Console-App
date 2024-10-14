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

import ua.SchoolConsoleApp.Course;

@ExtendWith(MockitoExtension.class)
public class CourseDAOTest {

	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private RowMapper<Course> courseRowMapper;

	@InjectMocks
	private CourseDAO courseDAO;
	private Course testCourse;

	@BeforeEach
	public void setup() {
		testCourse = new Course(1, "Test Name", "Test Description");
	}

	@Test
    public void create_schouldCreateCourse() {
            when(jdbcTemplate.update(anyString(),anyString(),anyString())).thenReturn(1);
        			courseDAO.create(testCourse);	      
          verify(jdbcTemplate, times(1)).update(
        		    eq("INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)"),
        		    eq(testCourse.getName()),
        		    eq(testCourse.getDescription())
        		);
    }

	@Test
	public void create_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.update(anyString(), anyString(),anyString()))
	            .thenThrow(new DataAccessException("Database error") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    	courseDAO.create(testCourse);	
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq("INSERT INTO school.COURSES (course_name, course_description) VALUES (?, ?)"), eq(testCourse.getName()),eq(testCourse.getDescription()));
	}

	@Test
	public void read_shouldReturnResult_whenCourseFound(){		
		when(jdbcTemplate.query(eq("SELECT * FROM school.courses WHERE course_id = ?"), any(RowMapper.class), eq(1)))
				.thenReturn(Collections.singletonList(testCourse));
		Optional<Course> result = courseDAO.read(1);
		assertTrue(result.isPresent());
		assertEquals("Test Name", result.get().getName());
		assertEquals("Test Description", result.get().getDescription());
		verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.courses WHERE course_id = ?"),any(RowMapper.class),eq(1));
	}

	@Test
	public void read_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
	            .thenThrow(new DataAccessException("Failed to read course") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    	courseDAO.read(1);
	    });
	    assertEquals("Failed to read course", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.courses WHERE course_id = ?"), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnEmtyCourse_whenCourseNotFound() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Course> result = courseDAO.read(1);
	    assertFalse(result.isPresent());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.courses WHERE course_id = ?"), any(RowMapper.class),eq(1));
	}

	@Test
	public void update_schouldUpdateCourse() {
		courseDAO.update(testCourse);
		verify(jdbcTemplate, times(1)).update(
				"UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?",
				testCourse.getName(), testCourse.getDescription(), testCourse.getId());
	}

	@Test
	public void update_schouldThrowNullPointerException_whenCourseisNull() {
		assertThrows(NullPointerException.class, () -> {
			courseDAO.update(null);
		});
	}

	@Test
	public void update_shouldReturnDataAccessException() {
	    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyInt())).thenThrow(new DataAccessException("Database error") {});
	    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    	courseDAO.update(testCourse);
	    });	
	    
	    assertEquals("Database error",  exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq("UPDATE school.courses SET course_name = ?, course_description = ? WHERE course_id = ?"),eq(testCourse.getName()),eq (testCourse.getDescription()), eq(testCourse.getId()));
		}

	@Test
	public void delete_schouldPerformInOrder() {
		courseDAO.delete(1);
		InOrder inOrder = inOrder(jdbcTemplate);
		inOrder.verify(jdbcTemplate).update("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?", 1);
		inOrder.verify(jdbcTemplate).update("DELETE FROM School.COURSES WHERE course_id = ?", 1);
	}

	@Test
	public void delete_shouldReturnResult_WithDifferentIdValues() {
		courseDAO.delete(1);
		verify(jdbcTemplate).update("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?", 1);
		verify(jdbcTemplate).update("DELETE FROM School.COURSES WHERE course_id = ?", 1);
		courseDAO.delete(0);
		verify(jdbcTemplate).update("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?", 0);
		verify(jdbcTemplate).update("DELETE FROM School.COURSES WHERE course_id = ?", 0);
		courseDAO.delete(-1);
		verify(jdbcTemplate).update("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?", -1);
		verify(jdbcTemplate).update("DELETE FROM School.COURSES WHERE course_id = ?", -1);
	}

	@Test
    public void delete_shouldThrowDataAccessException_WhenDeletingFromStudentsCourses() {
        when(jdbcTemplate.update(eq("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?"), eq(1)))
                .thenThrow(new DataAccessException("Database error while deleting from STUDENTS_COURSES") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseDAO.delete(1);
        });
        assertEquals("Database error while deleting from STUDENTS_COURSES", exception.getMessage());
        verify(jdbcTemplate, times(0)).update("DELETE FROM School.COURSES WHERE course_id = ?", 1);
    }

	@Test
    public void delete_shouldThrowDataAccessException_WhenDeletingFromCourses() {
        when(jdbcTemplate.update(eq("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?"), eq(1)))
                .thenReturn(1);
        when(jdbcTemplate.update(eq("DELETE FROM School.COURSES WHERE course_id = ?"), eq(1)))
                .thenThrow(new DataAccessException("Database error while deleting from COURSES") {});
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseDAO.delete(1);
        });
        assertEquals("Database error while deleting from COURSES", exception.getMessage());
        verify(jdbcTemplate, times(1)).update("DELETE FROM School.STUDENTS_COURSES WHERE course_id = ?", 1);
        verify(jdbcTemplate, times(1)).update("DELETE FROM School.COURSES WHERE course_id = ?", 1);
    }

	@Test
	public void getAll_schouldReturnCourseList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testCourse));
	    List<Course> result = courseDAO.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test Name", result.get(0).getName());
	    assertEquals("Test Description", result.get(0).getDescription());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.COURSES"), any(RowMapper.class));
	}

	@Test
	public void getAll_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
	            .thenThrow(new DataAccessException("Failed to fetch courses") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    	courseDAO.getAll();
	    });
	    assertEquals("Failed to fetch courses", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.COURSES"), any(RowMapper.class));
	}

	@Test
	public void getCourseIdByName_shouldReturnCourseId_whenCourseExists() {
		
		when(jdbcTemplate.queryForObject(eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"),
				eq(Integer.class), eq(testCourse.getName()))).thenReturn(testCourse.getId());
		int result = courseDAO.getCourseIdByName(testCourse.getName());
		assertEquals(testCourse.getId(), result);
		verify(jdbcTemplate, times(1)).queryForObject(eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"),
				eq(Integer.class), eq(testCourse.getName()));
	}

	@Test
	public void getCourseIdByName_shouldReturnMinusOne_whenCourseNotFound() {	    
	     when(jdbcTemplate.queryForObject(
	            eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"), 
	            eq(Integer.class), 
	            eq(testCourse.getName())))
	        .thenThrow(new EmptyResultDataAccessException(1));
	    int result = courseDAO.getCourseIdByName(testCourse.getName());
	    assertEquals(-1, result);
	    verify(jdbcTemplate, times(1)).queryForObject(
	            eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"), 
	            eq(Integer.class), 
	            eq(testCourse.getName()));
	}

	@Test
	public void getCourseIdByName_shouldReturnMinusOne_whenDataAccessExceptionOccurs() {
	    when(jdbcTemplate.queryForObject(
	            eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"), 
	            eq(Integer.class), 
	            eq(testCourse.getName())))
	        .thenThrow(new DataAccessException("Database error while deleting from COURSES") {});
	int result = courseDAO.getCourseIdByName(testCourse.getName());
    assertEquals(-1, result);
    verify(jdbcTemplate, times(1)).queryForObject(
            eq("SELECT course_id FROM school.COURSES WHERE course_name = ?"), 
            eq(Integer.class), 
            eq(testCourse.getName()));
	
	}

	@Test
	public void isStudentEnrolled_shouldReturnTrue_whenStudentIsEnrolled() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()))).thenReturn(1);
		boolean result = courseDAO.isStudentEnrolled(studentId, testCourse.getId());
		assertTrue(result);
		verify(jdbcTemplate, times(1)).queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldReturnFalse_whenStudentIsNotEnrolled() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()))).thenReturn(0);
		boolean result = courseDAO.isStudentEnrolled(studentId, testCourse.getId());
		assertFalse(result);
		verify(jdbcTemplate, times(1)).queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldReturnFalse_whenEmptyResultDataAccessExceptionThrown() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId())))
				.thenThrow(new EmptyResultDataAccessException(1));
		boolean result = courseDAO.isStudentEnrolled(studentId, testCourse.getId());
		assertFalse(result);
		verify(jdbcTemplate, times(1)).queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()));
	}

	@Test
	public void isStudentEnrolled_shouldThrowRuntimeException_whenDataAccessExceptionThrown() {
		int studentId = 1;
		when(jdbcTemplate.queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId())))
				.thenThrow(new DataAccessException("Database error") {
				});
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			courseDAO.isStudentEnrolled(studentId, testCourse.getId());
		});
		assertEquals("Failed to check student enrollment", exception.getMessage());
		verify(jdbcTemplate, times(1)).queryForObject(
				eq("SELECT COUNT(*) FROM School.STUDENTS_COURSES WHERE student_id = ? AND course_id = ?"),
				eq(Integer.class), eq(studentId), eq(testCourse.getId()));
	}

	@Test
	public void getCoursesByStudentId_shouldReturnCourseList_whenCoursesExist() {

		List<Course> expectedCourses = Collections.singletonList(testCourse);
		when(jdbcTemplate.query(eq("SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
				+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?"),
				any(RowMapper.class), eq(testCourse.getId()))).thenReturn(expectedCourses);
		List<Course> result = courseDAO.getCoursesByStudentId(testCourse.getId());
		assertEquals(expectedCourses.size(), result.size());
		assertEquals(expectedCourses.get(0).getName(), result.get(0).getName());
		verify(jdbcTemplate, times(1))
				.query(eq("SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
						+ "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id "
						+ "WHERE sc.student_id = ?"), any(RowMapper.class), eq(testCourse.getId()));
	}

	@Test
	public void getCoursesByStudentId_shouldReturnEmptyList_whenNoCoursesExist() {
	    when(jdbcTemplate.query(
	            anyString(), 
	            any(RowMapper.class), 
	            eq(testCourse.getId())
	    )).thenReturn(Collections.emptyList());
	    List<Course> result = courseDAO.getCoursesByStudentId(testCourse.getId());
	    assertTrue(result.isEmpty());
	    verify(jdbcTemplate, times(1)).query(
	            eq("SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
	            + "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?"),
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
	        courseDAO.getCoursesByStudentId(testCourse.getId());
	    });
	    assertEquals("Failed to retrieve courses for the student", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(
	    		 eq("SELECT c.course_id, c.course_name, c.course_description FROM school.courses c "
	    		            + "INNER JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?"),
	    		 any(RowMapper.class),
	            eq(testCourse.getId())
	    );
	}

	@Test
	public void assignCourse_shouldAssignCourseSuccessfully() {
		int studentId = 1;
		courseDAO.assignCourse(studentId, testCourse.getId());
		verify(jdbcTemplate, times(1)).update(
				eq("INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)"), eq(studentId),
				eq(testCourse.getId()));
	}

	@Test
	public void assignCourse_shouldThrowDataAccessException() {
		int studentId = 1;
		when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenThrow(new DataAccessException("Database error") {
		});

		assertThrows(RuntimeException.class, () -> {
			courseDAO.assignCourse(studentId, testCourse.getId());
		});
		verify(jdbcTemplate, times(1)).update(
				eq("INSERT INTO School.STUDENTS_COURSES (student_id, course_id) VALUES (?, ?)"), eq(studentId),
				eq(testCourse.getId()));
	}
}
