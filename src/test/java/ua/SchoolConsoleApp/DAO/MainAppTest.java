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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import ua.SchoolConsoleApp.Group;
import ua.SchoolConsoleApp.MainApp;
import ua.SchoolConsoleApp.Student;

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

	@InjectMocks
	private MainApp mainApp;

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnGroups_WithValideInput()
			throws NoSuchFieldException, IllegalAccessException {
		int TestInput = 3;
		Group group1 = new Group(1, "Group A");
		Group group2 = new Group(2, "Group B");

		List<Group> allGroups = Arrays.asList(group1, group2);
		when(groupDAO.getAll()).thenReturn(allGroups);
		when(studentsDAO.getNumStudentsInGroup(1)).thenReturn(2);
		when(studentsDAO.getNumStudentsInGroup(2)).thenReturn(3);

		Scanner mockScanner = mock(Scanner.class);
		when(mockScanner.nextInt()).thenReturn(TestInput);

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);
		try {
			List<Group> result = mainApp.findGroupsWithLessOrEqualStudents();

			assertEquals(2, result.size());
			assertEquals(group1, result.get(0));
			assertEquals(group2, result.get(1));

			verify(groupDAO, times(1)).getAll();
			verify(studentsDAO, times(1)).getNumStudentsInGroup(1);
			verify(studentsDAO, times(1)).getNumStudentsInGroup(2);
		} finally {
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnGroups_WithFirstInvalidInputAndSecondValide()
			throws Exception {
		String input = "invalid\n3\n";
		Group group1 = new Group(1, "Group A");
		Group group2 = new Group(2, "Group B");

		List<Group> allGroups = Arrays.asList(group1, group2);
		when(groupDAO.getAll()).thenReturn(allGroups);
		when(studentsDAO.getNumStudentsInGroup(1)).thenReturn(2);
		when(studentsDAO.getNumStudentsInGroup(2)).thenReturn(3);

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		scannerField.set(mainApp, mockScanner);
		PrintStream originalErr = System.err;

		try {
			List<Group> result = mainApp.findGroupsWithLessOrEqualStudents();

			assertEquals(2, result.size());
			assertEquals(group1, result.get(0));
			assertEquals(group2, result.get(1));
		} finally {
			scannerField.set(mainApp, originalScanner);
			System.setErr(originalErr);
		}
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnInvalidInputMessage_WhennInvalidInput() throws Exception {
		String errorMesage = "Invalid input. Please enter a valid integer.";
		String input = "invalid\n3\n";
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		scannerField.set(mainApp, mockScanner);

		ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		PrintStream originalErr = System.err;
		System.setErr(new PrintStream(errorStreamCaptor));
		try {
			mainApp.findGroupsWithLessOrEqualStudents();

			String errorOutput = errorStreamCaptor.toString().trim();
			assertTrue(errorOutput.contains(errorMesage));
		} finally {
			scannerField.set(mainApp, originalScanner);
			System.setOut(originalOut);
			System.setErr(originalErr);
		}
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnInvalidInputMessage_WhennNoGruppFind() throws Exception {
		String testInput = "2";
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(testInput.getBytes()));
		scannerField.set(mainApp, mockScanner);

		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			mainApp.findGroupsWithLessOrEqualStudents();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Groups not found."));
		} finally {
			scannerField.set(mainApp, originalScanner);
			System.setOut(originalOut);
		}
	}

	@Test
	public void findStudentsByCourseName_shouldReturnStudents_WithValideInput()
			throws NoSuchFieldException, IllegalAccessException {
		String TestGroupName = "Quadrober";
		int TestGroupId = 2;
		Student student1 = new Student(1, 2, "Gary", "Potter");
		Student student2 = new Student(2, 2, "Germiona", "Granger");
		Student student3 = new Student(3, 2, "Ron", "Weasley");

		List<Student> allStudents = Arrays.asList(student1, student2, student3);
		when(courseDAO.getCourseIdByName(TestGroupName)).thenReturn(TestGroupId);
		when(studentsDAO.getStudentsByCourseName(TestGroupName)).thenReturn(allStudents);

		Scanner mockScanner = mock(Scanner.class);
		when(mockScanner.nextLine()).thenReturn(TestGroupName);

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);
		try {
			List<Student> result = mainApp.findStudentsByCourseName();

			assertEquals(3, result.size());
			assertEquals(student1, result.get(0));
			assertEquals(student2, result.get(1));
			assertEquals(student3, result.get(2));

			verify(courseDAO, times(1)).getCourseIdByName(TestGroupName);
			verify(studentsDAO, times(1)).getStudentsByCourseName(TestGroupName);
		} finally {
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void findStudentsByCourseName_shouldReturnEmptyList_WithValideInput()
			throws NoSuchFieldException, IllegalAccessException {
		String TestCourseName = "Quadrober";
		int TestGroupId = 2;

		when(courseDAO.getCourseIdByName(TestCourseName)).thenReturn(TestGroupId);
		when(studentsDAO.getStudentsByCourseName(TestCourseName)).thenReturn(Collections.emptyList());

		Scanner mockScanner = mock(Scanner.class);
		when(mockScanner.nextLine()).thenReturn(TestCourseName);

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);
		try {
			List<Student> result = mainApp.findStudentsByCourseName();

			assertTrue(result.isEmpty());

			verify(courseDAO, times(1)).getCourseIdByName(TestCourseName);
			verify(studentsDAO, times(1)).getStudentsByCourseName(TestCourseName);
		} finally {
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void addNewStudent_shouldAddStudent_WhenValidInput() throws Exception {
		String input = "John\nDoe\n";
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);

		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStreamCaptor));

		try {
			doNothing().when(studentsDAO).create(any(Student.class));

			mainApp.addNewStudent();

			String output = outputStreamCaptor.toString().trim();
			assertTrue(output.contains("Enter the student's name:"));
			assertTrue(output.contains("Enter the student's surname:"));
			assertTrue(output.contains("The student has been successfully added."));

			verify(studentsDAO, times(1)).create(new Student("John", "Doe"));
		} finally {
			System.setOut(originalOut);
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void addNewStudent_shouldntAddStudents_withEmptyName() throws Exception {
		String input = "\n\n";
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);

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
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void addNewStudent_shouldntAddStudents_withEmptySurName() throws Exception {
		String input = "Garry\n\n";
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);

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
			scannerField.set(mainApp, originalScanner);
		}
	}

	@Test
	public void deleteStudent_shouldPrintErrorMessage_WhenInputIsInvalid() throws Exception {
		String input = "invalid\n1\n";
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));
		System.setErr(new PrintStream(outputStream));

		try {

			mainApp.deleteStudent();
			String output = outputStream.toString().trim();
			assertTrue(output.contains("Invalid input. Please enter a valid integer ID."));
		} finally {
			scannerField.set(mainApp, originalScanner);
			System.setOut(originalOut);
		}
	}

	@Test
	public void deleteStudent_shouldDeleteStudent_WhenValidIdIsProvided() throws Exception {
		int validId = 1;
		Student student = new Student("John", "Doe");
		when(studentsDAO.read(validId)).thenReturn(Optional.of(student));

		String input = validId + "\n";
		Field scannerField = MainApp.class.getDeclaredField("scanner");
		scannerField.setAccessible(true);
		Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		Scanner originalScanner = (Scanner) scannerField.get(mainApp);
		scannerField.set(mainApp, mockScanner);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));

		try {

			mainApp.deleteStudent();
			verify(studentsDAO, times(1)).delete(validId);
			String output = outputStream.toString().trim();
			assertTrue(output.contains("The student John Doe has been successfully deleted."));
		} finally {
			scannerField.set(mainApp, originalScanner);
			System.setOut(originalOut);
		}
	}
//наступний тест не працює, скотиняка така
	@Test
	public void deleteStudent_shouldPrintErrorMessage_WhenStudentNotFound() throws Exception {
	    int nonExistentId = 99;
	    when(studentsDAO.read(nonExistentId)).thenReturn(Optional.empty()); 

	    String input = nonExistentId + "\n";
	    Field scannerField = MainApp.class.getDeclaredField("scanner");
	    scannerField.setAccessible(true);
	    Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
	    Scanner originalScanner = (Scanner) scannerField.get(mainApp);
	    scannerField.set(mainApp, mockScanner);

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PrintStream originalOut = System.out;
	    System.setOut(new PrintStream(outputStream));

	    try {
	        mainApp.deleteStudent();  
	        verify(studentsDAO, never()).delete(nonExistentId);  
	        
	        String output = outputStream.toString().trim();
	        assertTrue(output.contains("Student with ID " + nonExistentId + " does not exist."));
	    } finally {
	        scannerField.set(mainApp, originalScanner);
	        System.setOut(originalOut);
	    }
	}
}
