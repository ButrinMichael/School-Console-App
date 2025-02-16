package ua.SchoolConsoleApp.DAO;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import ua.SchoolConsoleApp.Services.*;
import ua.schoolconsoleapp.Course;
import ua.schoolconsoleapp.Group;
import ua.schoolconsoleapp.MainApp;
import ua.schoolconsoleapp.Student;
import ua.schoolconsoleapp.dao.CourseDAO;
import ua.schoolconsoleapp.dao.GroupDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.services.GroupService;
import ua.schoolconsoleapp.services.StudentService;

@ExtendWith(MockitoExtension.class)
public class MainAppTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Mock
	private StudentsDAO studentsDAO;

	@Mock
	private GroupDAO groupDAO;

	@Mock
	private CourseDAO courseDAO;
	@Mock
	private StudentService studentService;
	@Mock
	private GroupService groupService;

	@InjectMocks
	private MainApp mainApp;

	private Scanner originalScanner;

	private void setMockScanner(String input) throws Exception {
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		scannerField.set(mainApp, mockScanner);
	}

	@BeforeEach
	void setUp() throws Exception {
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		MockitoAnnotations.openMocks(this);
		originalScanner = (Scanner) scannerField.get(mainApp);
	}

	@AfterEach
	void tearDown() throws Exception {
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		scannerField.set(mainApp, originalScanner);
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldCallGroupService() throws Exception {
		String input = "3\n";
		setMockScanner(input);

		List<Group> mockGroups = List.of(new Group(1, "Group A"), new Group(2, "Group B"));
		when(groupService.findGroupsWithLessOrEqualStudents(3)).thenReturn(mockGroups);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));

		try {
			mainApp.findGroupsWithLessOrEqualStudents();

			verify(groupService, times(1)).findGroupsWithLessOrEqualStudents(3);

			String output = outputStream.toString().trim();
			assertTrue(output.contains("Groups with number of students less than or equal to 3:"));
			assertTrue(output.contains("Group A"));
			assertTrue(output.contains("Group B"));
		} finally {
			System.setOut(originalOut);
		}
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnInvalidInputMessage_WhennInvalidInput() throws Exception {
		String errorMesage = "Invalid input. Please enter a valid integer.";
		String input = "invalid\n3\n";
		setMockScanner(input);
		ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalErr = System.err;
		System.setErr(new PrintStream(errorStreamCaptor));
		try {
			mainApp.findGroupsWithLessOrEqualStudents();

			String errorOutput = errorStreamCaptor.toString().trim();
			assertTrue(errorOutput.contains(errorMesage));
		} finally {
			System.setErr(originalErr);
		}
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnInvalidInputMessage_WhennNoGruppFind() throws Exception {
		String testInput = "2";
		setMockScanner(testInput);
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));
		try {
			mainApp.findGroupsWithLessOrEqualStudents();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Groups not found."));
		} finally {
			System.setOut(originalOut);
		}
	}

	@Test
	public void findStudentsByCourseName_shouldCallStudentService() throws Exception {
		String input = "Math\n";
		setMockScanner(input);

		List<Student> mockStudents = List.of(new Student(1, 1, "Alice", "Smith"), new Student(2, 1, "Bob", "Johnson"));

		when(studentService.findStudentsByCourseName("Math")).thenReturn(mockStudents);

		mainApp.findStudentsByCourseName();

		verify(studentService, times(1)).findStudentsByCourseName("Math");
	}

	@Test
	public void addNewStudent_shouldAddStudent_WhenValidInput() throws Exception {
		String input = "John\nDoe\n";
		setMockScanner(input);
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			doNothing().when(studentService).addNewStudent(any(Student.class));

			mainApp.addNewStudent();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Enter the student's name:"));
			assertTrue(output.contains("Enter the student's surname:"));
			assertTrue(output.contains("The student has been successfully added."));

			verify(studentService, times(1)).addNewStudent(new Student("John", "Doe"));
		} finally {
			System.setOut(originalOut);
		}
	}

	@Test
	public void addNewStudent_shouldntAddStudents_withEmptyName() throws Exception {
		String input = "\n\n";
		setMockScanner(input);
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));
		try {
			mainApp.addNewStudent();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Enter the student's name:"));
			assertTrue(output.contains("Name cannot be empty."));

			verify(studentsDAO, never()).create(any(Student.class));
		} finally {
			System.setOut(originalOut);
		}
	}

	@Test
	public void addNewStudent_shouldntAddStudents_withEmptySurName() throws Exception {
		String input = "Garry\n\n";
		setMockScanner(input);
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			mainApp.addNewStudent();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Enter the student's name:"));
			assertTrue(output.contains("Enter the student's surname:"));
			assertTrue(output.contains("Surname cannot be empty."));
			verify(studentsDAO, never()).create(any(Student.class));
		} finally {
			System.setOut(originalOut);
		}
	}

	@Test
	public void deleteStudent_shouldCallStudentService_WhenValidIdIsProvided() throws Exception {
		int validId = 1;
		String input = validId + "\n";
		setMockScanner(input);

		doNothing().when(studentService).deleteStudentById(validId);

		mainApp.deleteStudent();

		verify(studentService, times(1)).deleteStudentById(validId);
	}

	@Test
	public void deleteStudent_shouldNotCallStudentService_WhenInputIsInvalid() throws Exception {
		String input = "invalid\n1\n";
		setMockScanner(input);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalErr = System.err;
		System.setErr(new PrintStream(outputStream));

		try {
			mainApp.deleteStudent();
			String output = outputStream.toString().trim();
			assertTrue(output.contains("Invalid input. Please enter a valid integer ID."));
		} finally {
			System.setErr(originalErr);
		}

		verify(studentService, never()).deleteStudentById(anyInt());
	}

	@Test
	public void addStudentToCourse_shouldNotCallService_WhenStudentNotFound() throws Exception {
	
		when(courseDAO.getAll()).thenReturn(List.of(new Course(1, "Math")));
		doThrow(new RuntimeException("Student not found: John Doe"))
        .when(studentService).addStudentToCourse("John", "Doe", "Math");
		String input = "John\nDoe\nMath\n";
	    setMockScanner(input);

		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));
	    try {
	        mainApp.addStudentToCourse();

	        String output = outputStreamCaptor.toString().trim();
	        System.out.println("xcdsaf" + output);
	        assertTrue(output.contains("Error: Student not found: John Doe"));

	        
	       verify(studentService, times(1)).addStudentToCourse("John", "Doe", "Math");
	    } finally {
	        System.setOut(System.out); 
	    }
	}

	@Test
	public void addStudentToCourse_shouldNotCallService_WhenCourseNotFound() throws Exception {
		String input = "John\nDoe\nArt\n";
		setMockScanner(input);

		List<Course> courses = List.of(new Course(1, "Math"));
		when(courseDAO.getAll()).thenReturn(courses);

		doThrow(new RuntimeException("Course not found: Art")).when(studentService).addStudentToCourse("John", "Doe",
				"Art");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		try {
			mainApp.addStudentToCourse();

			String output = outputStream.toString().trim();
			assertTrue(output.contains("Error: Course not found: Art"));

			verify(studentService, times(1)).addStudentToCourse("John", "Doe", "Art");
		} finally {
			System.setOut(System.out);
		}
	}

	@Test
	public void addStudentToCourse_shouldCallService_WhenInputIsValid() throws Exception {
		String input = "John\nDoe\nMath\n";
		setMockScanner(input);

		List<Course> courses = List.of(new Course(1, "Math"));
		when(courseDAO.getAll()).thenReturn(courses);
		doNothing().when(studentService).addStudentToCourse("John", "Doe", "Math");

		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			mainApp.addStudentToCourse();

			verify(studentService).addStudentToCourse("John", "Doe", "Math");

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("The student has been successfully added to the course!"));
		} finally {
			System.setOut(System.out);
		}
	}

	@Test
	public void removeStudentFromCourse_shouldCallService_WhenValidInput() throws Exception {
		String input = "Alice\nJohnson\nMath\n";
		setMockScanner(input);

		when(studentsDAO.getStudentIdByName("Alice", "Johnson")).thenReturn(1);
		when(courseDAO.getCoursesByStudentId(1)).thenReturn(List.of(new Course(101, "Math")));
		doNothing().when(studentService).removeStudentFromCourse("Alice", "Johnson", "Math");

		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			mainApp.removeStudentFromCourse();

			verify(studentService, times(1)).removeStudentFromCourse("Alice", "Johnson", "Math");

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("The student has been successfully removed from the course!"));
		} finally {
			System.setOut(System.out);
		}
	}

	@Test
	public void removeStudentFromCourse_shouldNotCallService_WhenStudentNotFound() throws Exception {
		String input = "Jane\nDoe\nMath\n";
		setMockScanner(input);

		when(studentsDAO.getStudentIdByName("Jane", "Doe")).thenReturn(-1);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		try {
			mainApp.removeStudentFromCourse();
	        String output = outputStream.toString().trim();
	        System.out.println("Console Output: " + output);	        
			verify(studentService, never()).removeStudentFromCourse(anyString(), anyString(), anyString());
	
			assertTrue(output.contains("Error: Student not found: Jane Doe"));
		} finally {
			System.setOut(System.out);
		}
	}

	@Test
	public void removeStudentFromCourse_shouldPrintMessage_WhenNoEnrolledCourses() throws Exception {
		String input = "Jane\nDoe\n";
		setMockScanner(input);

		when(studentsDAO.getStudentIdByName("Jane", "Doe")).thenReturn(1);
		when(courseDAO.getCoursesByStudentId(1)).thenReturn(Collections.emptyList());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		try {

			mainApp.removeStudentFromCourse();

			verify(studentService, never()).removeStudentFromCourse(anyString(), anyString(), anyString());
			String output = outputStream.toString().trim();
			assertTrue(output.contains("The student is not enrolled in any course."));
		} finally {
			System.setOut(System.out);
		}
	}

}
