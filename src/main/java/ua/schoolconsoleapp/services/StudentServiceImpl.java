package ua.schoolconsoleapp.services;


import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.dao.CourseDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.models.Student;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService {

    private final CourseDAO courseDAO;
    private final StudentsDAO studentsDAO;

    public StudentServiceImpl(CourseDAO courseDAO, StudentsDAO studentsDAO) {
        this.courseDAO = courseDAO;
        this.studentsDAO = studentsDAO;
    }

    @Override
    public List<Student> findStudentsByCourseName(String courseName) {
        List<Student> result = new ArrayList<>();
        int courseId = courseDAO.getCourseIdByName(courseName);

        if (courseId == -1) {
            return result; 
        }

        List<Student> students = studentsDAO.getStudentsByCourseName(courseName);

        if (!students.isEmpty()) {
            result.addAll(students);
        }

        return result;
    }
    @Override
    public void addNewStudent(Student student) {
        if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Name or surname cannot be empty.");
        }
        studentsDAO.create(student);         
    }
    
    @Override
    public void deleteStudentById(int studentId) {
        try {
            Optional<Student> studentOpt = studentsDAO.read(studentId);
            if (!studentOpt.isPresent()) {
                throw new RuntimeException("Student with ID " + studentId + " does not exist.");
            }

            studentsDAO.delete(studentId);

            Student student = studentOpt.get();
            System.out.println("The student " + student.getFirstName() + " " + student.getLastName() +
                    " has been successfully deleted.");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete student: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addStudentToCourse(String studentName, String studentLastName, String courseName) {
        int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
        if (studentId == -1) {
            throw new RuntimeException("Student not found: " + studentName + " " + studentLastName);
        }

        int courseId = courseDAO.getCourseIdByName(courseName);
        if (courseId == -1) {
            throw new RuntimeException("Course not found: " + courseName);
        }

        boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
        if (isEnrolled) {
            throw new RuntimeException("The student is already enrolled in the course: " + courseName);
        }

        studentsDAO.addCourseToStudent(studentId, courseId);
    }
    
    @Override
    public void removeStudentFromCourse(String studentName, String studentLastName, String courseName) {
        int studentId = studentsDAO.getStudentIdByName(studentName, studentLastName);
        if (studentId == -1) {
            throw new RuntimeException("Student not found: " + studentName + " " + studentLastName);
        }

        int courseId = courseDAO.getCourseIdByName(courseName);
        if (courseId == -1) {
            throw new RuntimeException("Course not found: " + courseName);
        }

        boolean isEnrolled = courseDAO.isStudentEnrolled(studentId, courseId);
        if (!isEnrolled) {
            throw new RuntimeException("The student is not enrolled in the specified course: " + courseName);
        }

        studentsDAO.removeStudentFromCourse(studentId, courseId);
    }

  }

