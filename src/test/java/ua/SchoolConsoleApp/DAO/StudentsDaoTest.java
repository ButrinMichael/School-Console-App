package ua.SchoolConsoleApp.DAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
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
import ua.SchoolConsoleApp.Student;

@ExtendWith(MockitoExtension.class)
public class StudentsDaoTest {
	private static final String INSERT_STUDENT_SQL = "INSERT INTO school.STUDENTS (group_id, first_name, last_name) VALUES (?, ?, ?)";
	private static final String UPDATE_STUDENT_SQL = "UPDATE school.students SET group_id = ?, first_name = ?, last_name = ? WHERE student_id = ?";
	private static final String DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL = "DELETE FROM School.STUDENTS_COURSES WHERE student_id = ?";
	private static final String DELETE_STUDENT_BY_ID_SQL = "DELETE FROM school.students WHERE student_id = ?";
	private static final String SELECT_STUDENT_BY_ID_SQL = "SELECT * FROM school.students WHERE student_id = ?";
	private static final String SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL = "SELECT COUNT(*) FROM school.students WHERE group_id = ?";
	private static final String SELECT_STUDENTS_BY_COURSE_NAME_SQL = "SELECT s.* FROM school.students s "
			+ "JOIN school.students_courses sc ON s.student_id = sc.student_id "
			+ "JOIN school.courses c ON sc.course_id = c.course_id " + "WHERE c.course_name = ?";
	private static final String SELECT_STUDENTS_ID_BY_NAME_SQL = "SELECT student_id FROM school.students WHERE first_name = ? AND last_name = ? AND (group_id IS NULL OR group_id IS NOT NULL)";
	private static final String DELETE_STUDENT_FROM_CURSE_SQL = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
	private static final String DELETE_COURSE_FROM_STUDENT_SQL = "DELETE FROM school.students_courses WHERE student_id = ? AND course_id = ?";
	private static final String INSERT_COURSE_TO_STUDENT_SQL = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
	private static final String SELECT_COURSE_BY_STUDENT_ID_SQL = "SELECT c.* FROM school.courses c "
			+ "JOIN school.students_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";
	private static final String SELECT_STUDENT_BY_COURSE_ID_SQL = "SELECT s.* FROM school.students s "
			+ "JOIN school.students_courses sc ON s.student_id = sc.student_id " + "WHERE sc.course_id = ?";

	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private RowMapper<Student> studentRowMapper;


	@InjectMocks
	private StudentsDAO studentsDAO;
	private Student testStudent;

	@BeforeEach
	public void setup() {
		testStudent = new Student(1, 2, "Test firstName", "Test lastName");
	}

	@Test
	public void create_schouldCreateStudent() {
		studentsDAO.create(testStudent);
		verify(jdbcTemplate, times(1)).update(eq(INSERT_STUDENT_SQL), eq(testStudent.getGroupId()),
				eq(testStudent.getFirstName()), eq(testStudent.getLastName()));
	}

	@Test
	public void update_shouldUpdateStudentDetails() {

		studentsDAO.update(testStudent);
		verify(jdbcTemplate, times(1)).update(eq(UPDATE_STUDENT_SQL), eq(testStudent.getGroupId()),
				eq(testStudent.getFirstName()), eq(testStudent.getLastName()), eq(testStudent.getId()));
	}

	@Test
	public void update_shouldThrowRuntimeException_whenDataAccessExceptionOccurs() {

		doThrow(new DataAccessException("Database error") {
		}).when(jdbcTemplate).update(anyString(), any(), any(), any(), any());

		DataAccessException exception = assertThrows(DataAccessException.class, () -> {
			studentsDAO.update(testStudent);
		});

		assertEquals("Database error", exception.getMessage());
	}

	@Test
	public void delete_shouldDeleteStudent_WithPositiveId() {
		studentsDAO.delete(1);
		verify(jdbcTemplate).update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, 1);
		verify(jdbcTemplate).update(DELETE_STUDENT_BY_ID_SQL, 1);
	}

	@Test
	public void delete_shouldDeleteStudent_WithZeroId() {
		studentsDAO.delete(0);
		verify(jdbcTemplate).update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, 0);
		verify(jdbcTemplate).update(DELETE_STUDENT_BY_ID_SQL, 0);
	}

	@Test
	public void delete_shouldDeleteStudent_WithNegativeId() {
		studentsDAO.delete(-1);
		verify(jdbcTemplate).update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, -1);
		verify(jdbcTemplate).update(DELETE_STUDENT_BY_ID_SQL, -1);
	}

	@Test
	public void delete_shouldThrowRuntimeException_WhenDataAccessExceptionOccurs() {
		doThrow(new DataAccessException("Database error") {
		}).when(jdbcTemplate).update(DELETE_STUDENT_FROM_STUDENT_COURSES_BY_ID_SQL, 1);
		DataAccessException exception = assertThrows(DataAccessException.class, () -> studentsDAO.delete(1));
		assertEquals("Database error", exception.getMessage());
	}

	@Test
	public void read_shouldReturnResult_whenStudentFound() {    
	    when(jdbcTemplate.query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class), eq(1)))
	            .thenReturn(Collections.singletonList(testStudent));
	    Optional<Student> result = studentsDAO.read(1);
	    assertTrue(result.isPresent(), "Expected a student to be present in the Optional");
	    assertEquals(testStudent, result.get(), "The student returned does not match the expected student");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
	            .thenThrow(new DataAccessException("Failed to read student") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	studentsDAO.read(1);
	    });
	    assertEquals("Failed to read student", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnEmptyStudent_whenStudentNotFound() {
	      when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Student> result = studentsDAO.read(1);
	    assertFalse(result.isPresent(), "Expected no student to be present in the Optional when none is found");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void getAll_schouldReturnStudentsList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testStudent));
	    List<Student> result = studentsDAO.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test firstName", result.get(0).getFirstName());
	    assertEquals("Test lastName", result.get(0).getLastName());
	    assertEquals(2, result.get(0).getGroupId());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class));
	}

	@Test
	public void getAll_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
	            .thenThrow(new DataAccessException("Failed to fetch Students") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	    	studentsDAO.getAll();
	    });
	    assertEquals("Failed to fetch Students", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENT_BY_ID_SQL), any(RowMapper.class));
	}

	@Test
	public void getNumStudentsInGroup_shouldReturnCount_whenStudentsExist() {
		int expectedCount = 5;
		when(jdbcTemplate.queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class,
				testStudent.getGroupId())).thenReturn(expectedCount);
		int result = studentsDAO.getNumStudentsInGroup(testStudent.getGroupId());
		assertEquals(expectedCount, result);
		verify(jdbcTemplate, times(1)).queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class,
				testStudent.getGroupId());
	}

	@Test
    public void getNumStudentsInGroup_shouldReturnZero_whenNoStudentsFound() {
        when(jdbcTemplate.queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, testStudent.getGroupId()))
                .thenThrow(new EmptyResultDataAccessException(1));
        int result = studentsDAO.getNumStudentsInGroup(testStudent.getGroupId());
        assertEquals(0, result);
        verify(jdbcTemplate, times(1)).queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, testStudent.getGroupId());
    }

	@Test
    public void getNumStudentsInGroup_shouldThrowRuntimeException_whenDataAccessExceptionOccurs() {
           when(jdbcTemplate.queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, testStudent.getGroupId()))
                .thenThrow(new DataAccessException("Database error") {});
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentsDAO.getNumStudentsInGroup(testStudent.getGroupId());
        });
        assertEquals("Failed to fetch number of students", exception.getMessage());
        verify(jdbcTemplate, times(1)).queryForObject(SELECT_COUNT_STUDENTS_BY_GROUP_ID_SQL, Integer.class, testStudent.getGroupId());
    }

//	@Test
//    public void getStudentsByCourseName_shouldReturnStudents_whenCourseNameExists() {
//		String courseName = "Art";
//	    Student student1 = new Student(1,1, "John", "Doe");
//	    Student student2 = new Student(2,1, "Jane", "Smith");
//	    List<Student> expectedStudents = List.of(student1, student2);
//
//	    System.out.println("SQL Query: " + SELECT_STUDENTS_BY_COURSE_NAME_SQL);
//	    System.out.println("RowMapper: " + studentRowMapper);
//	    System.out.println("Course Name: " + courseName);
//	    when(jdbcTemplate.query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName)))
//        .thenReturn(expectedStudents);
//	    
//	    System.out.println(studentsDAO.getStudentsByCourseName("Art"));
//
//	    List<Student> result = studentsDAO.getStudentsByCourseName(courseName);
//	    assertEquals(expectedStudents, result);
//	    
//	    assertEquals(2, result.size());
//	    verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName));
//	}

	@Test
    public void getStudentsByCourseName_shouldReturnEmptyList_whenNoStudentsFound() {
        String courseName = "History";

        when(jdbcTemplate.query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName)))
                .thenReturn(Collections.emptyList());

        List<Student> result = studentsDAO.getStudentsByCourseName(courseName);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName));
    }

//    @Test
//    public void getStudentsByCourseName_shouldReturnEmptyList_whenDataAccessExceptionOccurs() {
//        String courseName = "Science";
//
//        when(jdbcTemplate.query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName)))
//                .thenThrow(new DataAccessException("Database error") {});
//
//        List<Student> result = studentsDAO.getStudentsByCourseName(courseName);
//
//        assertTrue(result.isEmpty());
//        verify(jdbcTemplate, times(1)).query(eq(SELECT_STUDENTS_BY_COURSE_NAME_SQL), eq(studentRowMapper), eq(courseName));
//    }
}
