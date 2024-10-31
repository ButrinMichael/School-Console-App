package ua.SchoolConsoleApp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ua.SchoolConsoleApp.DAO.CourseDAO;
import ua.SchoolConsoleApp.DAO.GroupDAO;
import ua.SchoolConsoleApp.DAO.StudentsDAO;

@SpringBootApplication
public class MainApp implements CommandLineRunner {
	private static void closeScanner() {
		if (scanner != null) {
			scanner.close();
		}
	}

	@Autowired
	private StudentsDAO studentsDAO;

	@Autowired
	private GroupDAO groupDAO;

	@Autowired
	private CourseDAO courseDAO;

	@Autowired
//	private DBInitializer dbInitializer;

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		SpringApplication.run(MainApp.class, args);
	}

	@Override
	public void run(String... args) {
		// dbInitializer.initializeDatabase();
		showMenu();
		closeScanner();
		System.exit(0);
	}

	private void showMenu() {
		boolean showMenuUntilTrue = true;
		while (showMenuUntilTrue == true) {
			System.out.println("Please select an action:");
			System.out.println("1. Find all groups with less or equal students’ number");
			System.out.println("2. Find all students related to the course with the given name");
			System.out.println("3. Add a new student");
			System.out.println("4. Delete a student by the STUDENT_ID");
			System.out.println("5. Add a student to the course (from a list)");
			System.out.println("6. Remove the student from one of their courses.");

			System.out.println("7. EXIT");

			int choice = scanner.nextInt();
			scanner.nextLine();
			switch (choice) {
			case 1:
				findGroupsWithLessOrEqualStudents();
				break;
			case 2:
				findStudentsByCourseName();
				break;
			case 3:
				addNewStudent();
				break;
			case 4:
				deleteStudent();
				break;
			case 5:
				addStudentToCourse();
				break;
			case 6:
				removeStudentFromCourse();
				break;
			case 7:
				System.out.println("Goodbuy");
				closeScanner();
				System.exit(0);
				break;
			default:
				System.out.println("Invalid choise");
			}
			System.out.println("Press 1 to continue or 2 to Exit");
			int choice2 = scanner.nextInt();
			scanner.nextLine();
			if (choice2 != 1) {
				System.out.println("Goodbuy");
				showMenuUntilTrue = false;
			}
		}
	}

	public List<Group> findGroupsWithLessOrEqualStudents() {
		List<Group> result = new ArrayList<>();
		System.out.println("Enter the maximum number of students:");
		int maxStudents;
		try {
			maxStudents = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Invalid input. Please enter a valid integer.");
			scanner.nextLine();
			findGroupsWithLessOrEqualStudents();
			return result;
		}
		try {
			List<Group> allGroups = groupDAO.getAll();
			for (Group group : allGroups) {
				int numStudents = studentsDAO.getNumStudentsInGroup(group.getId());
				if (numStudents <= maxStudents) {
					result.add(group);
				}
			}

			if (result.isEmpty()) {
				System.out.println("Groups not found.");
			} else {
				System.out.println("Groups with number of students less than or equal to " + maxStudents + ":");
				for (Group group : result) {
					System.out.println(group);
				}
			}
		} catch (RuntimeException e) {
			System.err.println("Failed to find groups: " + e.getMessage());
		}
		return result;

	}

	public List<Student> findStudentsByCourseName() {
		List<Student> result = new ArrayList<>();
		System.out.println("Enter the name of the course:");
		String courseName = scanner.nextLine().trim();
		int courseId;
		try {
			courseId = courseDAO.getCourseIdByName(courseName);
			if (courseId == -1) {
				return result;
			}

			List<Student> students = studentsDAO.getStudentsByCourseName(courseName);
			if (students.isEmpty()) {
				System.out.println("Students associated with the course \"" + courseName + "\" were not found.");
			} else {
				System.out.println("Students associated with the course \"" + courseName + "\":");
				for (Student student : students) {
					System.out.println(student.getFirstName() + " " + student.getLastName());
				}
				result.addAll(students);
			}
		} catch (Exception e) {
			System.err.println("An error occurred while finding students by course name: " + e.getMessage());
		}

		return result;
	}

	private void addNewStudent() {
		System.out.println("Enter the student's name:");
		String firstName = scanner.nextLine();
		System.out.println("Enter the student's surname:");
		String lastName = scanner.nextLine();

		Student student = new Student(firstName, lastName);

		studentsDAO.create(student);
		System.out.println("The student has been successfully added.");
	}

	private void deleteStudent() {
		System.out.println("Enter the student ID for deletion:");
		int studentId;
		try {
			studentId = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("Invalid input. Please enter a valid integer ID.");
			scanner.next();
			deleteStudent();
			return;
		}

		try {
			Optional<Student> studentOpt = studentsDAO.read(studentId);
			studentOpt.isPresent();
			studentsDAO.delete(studentId);
			Student student = studentOpt.get();
			System.out.println("The student " + student.getFirstName() + " " + student.getLastName()
					+ " has been successfully deleted.");

		} catch (RuntimeException e) {
			System.out.println("Failed to delete student: " + e.getMessage() + " Please try again.");
		}
	}

	public void addStudentToCourse() {
		System.out.println("Enter the student's name:");
		String studentName = scanner.nextLine().trim();

		System.out.println("Enter the student's surname:");
		String studentLastName = scanner.nextLine().trim();

		int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
		if (studentId == -1) {
			return;
		}

		List<Course> courses = courseDAO.getAll();
		if (courses.isEmpty()) {
			System.out.println("Course list is empty.");
			return;
		}

		System.out.println("Enter the name of the course from the list:");
		for (Course course : courses) {
			System.out.println(course.getName());
		}

		String courseName = scanner.nextLine().trim();

		int courseId = courseDAO.getCourseIdByName(courseName);
		if (courseId == -1) {
			return;
		}

		boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
		if (isEnrolled) {
			System.out.println("The student is already enrolled in this course.");
		} else {
			studentsDAO.addCourseToStudent(studentId, courseId);
			System.out.println("The student has been successfully added to the course!");
		}
	}

	public void removeStudentFromCourse() {
		System.out.println("Enter the student's name:");
		String studentName = scanner.nextLine().trim();
		System.out.println("Enter the student's surname:");
		String studentLastName = scanner.nextLine().trim();

		int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);

		if (studentId == -1) {
			return;
		}

		List<Course> enrolledCourses = courseDAO.getCoursesByStudentId(studentId);
		if (enrolledCourses.isEmpty()) {
			System.out.println("The student is not enrolled in any course.");
			return;
		}
		System.out.println("The student is enrolled in the following courses:");
		for (Course course : enrolledCourses) {
			System.out.println(course.getName());
		}

		System.out.println("Enter the name of the course to remove from the list:");
		String courseName = scanner.nextLine().trim();

		int courseId = courseDAO.getCourseIdByName(courseName);
		if (courseId == -1) {
			return;
		}

		boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
		if (!isEnrolled) {
			System.out.println("The student is not enrolled in the specified course.");
			return;
		}
		studentsDAO.removeStudentFromCourse(studentId, courseId);
	}

}
