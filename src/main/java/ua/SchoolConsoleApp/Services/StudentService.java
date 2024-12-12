package ua.SchoolConsoleApp.Services;

import ua.SchoolConsoleApp.Student;

import java.util.List;

public interface StudentService {
    List<Student> findStudentsByCourseName(String courseName);
    void addNewStudent(Student student);
    void deleteStudentById(int studentId);
    void addStudentToCourse(String studentName, String studentLastName, String courseName);
    void removeStudentFromCourse(String studentName, String studentLastName, String courseName);

}
