package ua.schoolconsoleapp.services;

import java.util.List;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;

public interface StudentService {
    List<Student> findStudentsByCourseName(String courseName);
    List<Course> getCoursesByStudentName(String firstName, String lastName);
    void addNewStudent(Student student);
    void deleteStudentById(int studentId);
    void addStudentToCourse(String studentName, String studentLastName, String courseName);
    void removeStudentFromCourse(String studentName, String studentLastName, String courseName);

}
