package ua.schoolconsoleapp.services;

import java.util.List;

import ua.schoolconsoleapp.Student;

public interface StudentService {
    List<Student> findStudentsByCourseName(String courseName);
    void addNewStudent(Student student);
    void deleteStudentById(int studentId);
    void addStudentToCourse(String studentName, String studentLastName, String courseName);
    void removeStudentFromCourse(String studentName, String studentLastName, String courseName);

}
