package ua.SchoolConsoleApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ua.SchoolConsoleApp.DAO.CourseDAO;
import ua.SchoolConsoleApp.DAO.GroupDAO;
import ua.SchoolConsoleApp.DAO.StudentsDAO;
import ua.SchoolConsoleApp.DB.DatabaseConnection;

public class MainApp {
	private static final Scanner scanner = new Scanner(System.in);

	private static void closeScanner() {
		if (scanner != null) {
			scanner.close();

		}
	}

	public static void main(String[] args) throws SQLException {
		// DBInitializer.initializeDatabase();

		showMenu();
		closeScanner();
	}

	private static void showMenu() throws SQLException {
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

	public static List<Group> findGroupsWithLessOrEqualStudents() throws SQLException {
		List<Group> result = new ArrayList<>();
		System.out.println("Enter the maximum number of students:");
		int maxStudents = scanner.nextInt();

		try (Connection connection = DatabaseConnection.getConnection()) {
			GroupDAO groupDAO = new GroupDAO(connection);
			List<Group> allGroups = groupDAO.getAll();
			StudentsDAO studentsDAO = new StudentsDAO(connection);

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
		} catch (SQLException e) {
			System.err.println("Error occurred while retrieving groups: " + e.getMessage());
			e.printStackTrace();
			showMenu(); 
		}

		return result;
	}

	public static List<Student> findStudentsByCourseName() throws SQLException {
		List<Student> result = new ArrayList<>();
		System.out.println("Enter the name of the course:");
		String courseName = scanner.nextLine().trim();

		try (Connection connection = DatabaseConnection.getConnection()) {
			StudentsDAO studentsDAO = new StudentsDAO(connection);
			List<Student> students = studentsDAO.getStudentsByCourseName(courseName);
			if (students.isEmpty()) {
				System.out.println("Students associated with the course  \"" + courseName + "\", were not found.");

			} else {
				System.out.println("Students associated with the course \"" + courseName + "\":");

				for (Student student : students) {
					System.out.println(student.getFirstName() + " " + student.getLastName());
				}
				result.addAll(students);
			}
		} catch (SQLException e) {
			System.err.println("Error occurred while retrieving groups: " + e.getMessage());
			e.printStackTrace();
			showMenu(); 
		}
		return result;

	}

	private static void addNewStudent() throws SQLException {
		System.out.println("Enter the student's name:");
		String firstName = scanner.nextLine();
		System.out.println("Enter the student's surname:");
		String lastName = scanner.nextLine();

		Student student = new Student(firstName, lastName);

		try (Connection connection = DatabaseConnection.getConnection()) {
			StudentsDAO studentsDAO = new StudentsDAO(connection);
			studentsDAO.create(student);
			System.out.println("The student has been successfully added.");
		} catch (SQLException e) {
			System.err.println("Error occurred while adding the student: " + e.getMessage());
			e.printStackTrace();
			showMenu(); 
		}
	}

	private static void deleteStudent() throws SQLException {

		System.out.println("Enter the student ID for deletion:");
		int studentId = scanner.nextInt();
		try (Connection connection = DatabaseConnection.getConnection()) {
			StudentsDAO studentsDAO = new StudentsDAO(connection);
			Student student = studentsDAO.read(studentId);
			if (student != null) {
				studentsDAO.delete(studentId);
				System.out.println("The student has been successfully deleted.");
			} else {
				System.out.println("Student with ID " + studentId + " does not exist.");
			}
		} catch (SQLException e) {
			System.err.println("Error occurred while deleting the student: " + e.getMessage());
			e.printStackTrace();
			showMenu(); 
		}
	}

	public static void addStudentToCourse() throws SQLException {
		System.out.println("Enter the student's name:");
		String studentName = scanner.nextLine().trim();

		System.out.println("Enter the student's surname:");
		String studentLastName = scanner.nextLine().trim();

		try (Connection connection = DatabaseConnection.getConnection()) {
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentsDAO studentsDAO = new StudentsDAO(connection);

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

			if (courseId != -1) {
				int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);

				if (studentId != -1) {
					boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);

					if (!isEnrolled) {
						String sql = "INSERT INTO school.students_courses (student_id, course_id) VALUES (?, ?)";
						try (PreparedStatement statement = connection.prepareStatement(sql)) {
							statement.setInt(1, studentId);
							statement.setInt(2, courseId);
							statement.executeUpdate();
							System.out.println("The student has been successfully added to the course!");
						}
					} else {
						System.out.println("The student is already enrolled in this course.");
					}
				} else {
					System.out.println("The student with the specified name and surname was not found.");
				}
			} else {
				System.out.println("The course with the specified name was not found.");
			}
		} catch (SQLException e) {
			System.err.println("Error occurred while adding the student to the course: " + e.getMessage());
			e.printStackTrace();
			showMenu(); 
		}
	}

	public static void removeStudentFromCourse() throws SQLException {
		System.out.println("Enter the student's name:");
		String studentName = scanner.nextLine().trim();
		System.out.println("Enter the student's surname:");
		String studentLastName = scanner.nextLine().trim();

		try (Connection connection = DatabaseConnection.getConnection()) {
			CourseDAO courseDAO = new CourseDAO(connection);
			StudentsDAO studentsDAO = new StudentsDAO(connection);
			int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
			if (studentId == -1) {
				System.out.println("The student with the specified name and surname was not found.");
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
				System.out.println("The course with the specified name was not found.");
				return;
			}

			boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
			if (!isEnrolled) {
				System.out.println("The student is not enrolled in the specified course.");
				return;
			}
			studentsDAO.removeStudentFromCourse(studentId, courseId);
		} catch (SQLException e) {
			System.err.println("Error occurred while removing the student from the course: " + e.getMessage());
			e.printStackTrace();
			showMenu();
		}

	}

}
