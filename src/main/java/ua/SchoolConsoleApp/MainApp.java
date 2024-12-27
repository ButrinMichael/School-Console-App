package ua.SchoolConsoleApp;


import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.SchoolConsoleApp.DAO.CourseDAO;
import ua.SchoolConsoleApp.DAO.StudentsDAO;
import ua.SchoolConsoleApp.Services.GroupService;
import ua.SchoolConsoleApp.Services.StudentService;



@SpringBootApplication(scanBasePackages = "ua.SchoolConsoleApp")
public class MainApp implements CommandLineRunner {
	private static void closeScanner() {
		if (scanner != null) {
			scanner.close();
		}
	}

	@Autowired
	private StudentsDAO studentsDAO;

	@Autowired
	private CourseDAO courseDAO;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private GroupService groupService;

//	@Autowired
//	private DBInitializer dbInitializer;
	
//	@Autowired
//	private final GroupService groupService;
//	public MainApp(GroupService groupService, Scanner scanner) {
//        this.groupService = groupService;
//        this.scanner = scanner;
//	}
	
	
	
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
	public void findGroupsWithLessOrEqualStudents(){
        int maxStudents = -1;

        while (maxStudents == -1) {
            System.out.println("Enter the maximum number of students:");
            try {
                maxStudents = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }

        try {
            List<Group> groups = groupService.findGroupsWithLessOrEqualStudents(maxStudents);
            if (groups.isEmpty()) {
                System.out.println("Groups not found.");
            } else {
                System.out.println("Groups with number of students less than or equal to " + maxStudents + ":");
                for (Group group : groups) {
                    System.out.println(group);
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
	
	public void findStudentsByCourseName() {
	    System.out.println("Enter the name of the course:");
	    String courseName = scanner.nextLine().trim();

	    try {
	        List<Student> students = studentService.findStudentsByCourseName(courseName);

	        if (students.isEmpty()) {
	            System.out.println("Students associated with the course \"" + courseName + "\" were not found.");
	        } else {
	            System.out.println("Students associated with the course \"" + courseName + "\":");
	            for (Student student : students) {
	                System.out.println(student.getFirstName() + " " + student.getLastName());
	            }
	        }
	    } catch (RuntimeException e) {
	        System.err.println("An error occurred while finding students by course name: " + e.getMessage());
	    }
	}
	public void addNewStudent() {
		System.out.println("Enter the student's name:");
		String firstName = scanner.nextLine();
		if (firstName.isEmpty()) {
	        System.out.println("Name cannot be empty.");
	        return;
	    }
		System.out.println("Enter the student's surname:");
		String lastName = scanner.nextLine();
		if (lastName.isEmpty()) {
	        System.out.println("Surname cannot be empty.");
	        return;
	    }
		Student student = new Student(firstName, lastName);

		try {	      
	        studentService.addNewStudent(student);
	        System.out.println("The student has been successfully added.");
	    } catch (IllegalArgumentException e) {
	        System.err.println("Error: " + e.getMessage());
	    } catch (RuntimeException e) {
	        System.err.println("Failed to add student: " + e.getMessage());
	    }
	}


	public void deleteStudent() {
	    int studentId = -1;
	    while (studentId == -1) {
	        System.out.println("Enter the student ID for deletion:");
	        try {
	            studentId = scanner.nextInt();
	            scanner.nextLine(); 
	        } catch (InputMismatchException e) {
	            System.err.println("Invalid input. Please enter a valid integer ID.");
	            scanner.nextLine(); 
	            return; 
	        }
	    }

	    try {
	        studentService.deleteStudentById(studentId);
	        System.out.println("Student successfully deleted.");
	    } catch (RuntimeException e) {
	        System.err.println("Failed to delete student: " + e.getMessage());
	    }
	}
	
	public void addStudentToCourse() {
	    try {	      
	        System.out.println("Enter the student's name:");
	        String studentName = scanner.nextLine().trim();
	        System.out.println("Enter the student's surname:");
	        String studentLastName = scanner.nextLine().trim();
	      
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

	        studentService.addStudentToCourse(studentName, studentLastName, courseName);

	        System.out.println("The student has been successfully added to the course!");
	    } catch (RuntimeException e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	}

	public void removeStudentFromCourse() {
	    try {
	        System.out.println("Enter the student's name:");
	        String studentName = scanner.nextLine().trim();
	        System.out.println("Enter the student's surname:");
	        String studentLastName = scanner.nextLine().trim();

	        int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
	        if (studentId == -1) {
	            System.out.println("Error: Student not found: " + studentName + " " + studentLastName);
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

	        studentService.removeStudentFromCourse(studentName, studentLastName, courseName);

	        System.out.println("The student has been successfully removed from the course!");
	    } catch (RuntimeException e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	}
	
}
