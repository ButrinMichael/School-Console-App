package ua.schoolconsoleapp;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
//sdfsdfs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.services.CourseService;
import ua.schoolconsoleapp.services.GroupService;
import ua.schoolconsoleapp.services.StudentService;
import ua.schoolconsoleapp.utils.DBInitializer;

@SpringBootApplication(scanBasePackages = "ua.schoolconsoleapp")
public class MainApp implements CommandLineRunner {

    private static final Scanner scanner = new Scanner(System.in);

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private DBInitializer dbInitializer;

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

    public void findGroupsWithLessOrEqualStudents() {
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

            List<Course> courses = courseService.getAllCourses();
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
            
            List<Course> enrolledCourses = studentService.getCoursesByStudentName(studentName, studentLastName);

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

    private static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    
    private void showMenu() {
        boolean showMenuUntilTrue = true;
        while (showMenuUntilTrue) {
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
                case 1 -> findGroupsWithLessOrEqualStudents();
                case 2 -> findStudentsByCourseName();
                case 3 -> addNewStudent();
                case 4 -> deleteStudent();
                case 5 -> addStudentToCourse();
                case 6 -> removeStudentFromCourse();
                case 7 -> {
                    System.out.println("Goodbye");
                    closeScanner();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }

            System.out.println("Press 1 to continue or 2 to Exit");
            int choice2 = scanner.nextInt();
            scanner.nextLine();
            if (choice2 != 1) {
                System.out.println("Goodbye");
                showMenuUntilTrue = false;
            }
        }
    }
}
