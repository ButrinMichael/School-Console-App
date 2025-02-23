package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import ua.schoolconsoleapp.dao.CourseDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.services.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private CourseDAO courseDAO;

    @Mock
    private StudentsDAO studentsDAO;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public void findStudentsByCourseName_shouldReturnStudents_WhenCourseAndStudentsExist() {
        String courseName = "Math";
        int courseId = 1;
        List<Student> students = List.of(
                new Student(1, 1, "Alice", "Smith"),
                new Student(2, 1, "Bob", "Johnson")
        );

        when(courseDAO.getCourseIdByName(courseName)).thenReturn(courseId);
        when(studentsDAO.getStudentsByCourseName(courseName)).thenReturn(students);

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertEquals(2, result.size());
        assertEquals(students, result);
        verify(courseDAO, times(1)).getCourseIdByName(courseName);
        verify(studentsDAO, times(1)).getStudentsByCourseName(courseName);
    }
    @Test
    public void findStudentsByCourseName_shouldReturnEmptyList_WhenCourseNotFound() {
        String courseName = "UnknownCourse";

        when(courseDAO.getCourseIdByName(courseName)).thenReturn(-1);

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertTrue(result.isEmpty());
        verify(courseDAO, times(1)).getCourseIdByName(courseName);
        verify(studentsDAO, never()).getStudentsByCourseName(anyString());
    }

  
    @Test
    public void findStudentsByCourseName_shouldReturnEmptyList_WhenNoStudentsOnCourse() {
        String courseName = "Math";
        int courseId = 1;

        when(courseDAO.getCourseIdByName(courseName)).thenReturn(courseId);
        when(studentsDAO.getStudentsByCourseName(courseName)).thenReturn(List.of());

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertTrue(result.isEmpty());
        verify(courseDAO, times(1)).getCourseIdByName(courseName);
        verify(studentsDAO, times(1)).getStudentsByCourseName(courseName);
    }
    
    @Test
    public void addNewStudent_shouldCallDAOCreate_WhenStudentIsValid() {
        Student validStudent = new Student("John", "Doe");

        studentService.addNewStudent(validStudent);

        verify(studentsDAO, times(1)).create(validStudent);
    }
    
    @Test
    public void addNewStudent_shouldThrowException_WhenFirstNameIsEmpty() {
        Student invalidStudent = new Student("", "Doe");

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> studentService.addNewStudent(invalidStudent));

        assertEquals("Name or surname cannot be empty.", exception.getMessage());
        verify(studentsDAO, never()).create(any());
    }
    
    @Test
    public void addNewStudent_shouldThrowException_WhenLastNameIsEmpty() {
        Student invalidStudent = new Student("John", "");

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> studentService.addNewStudent(invalidStudent));

        assertEquals("Name or surname cannot be empty.", exception.getMessage());
        verify(studentsDAO, never()).create(any());
    }
    @Test
    public void deleteStudentById_shouldDeleteStudent_WhenValidId() {

        int studentId = 1;
        Student student = new Student("John", "Doe");
        when(studentsDAO.read(studentId)).thenReturn(Optional.of(student));
        doNothing().when(studentsDAO).delete(studentId);


        studentService.deleteStudentById(studentId);


        verify(studentsDAO, times(1)).read(studentId);
        verify(studentsDAO, times(1)).delete(studentId);
    }

    @Test
    public void deleteStudentById_shouldThrowException_WhenStudentDoesNotExist() {

        int studentId = 1;
        when(studentsDAO.read(studentId)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.deleteStudentById(studentId));
        assertEquals("Failed to delete student: Student with ID 1 does not exist.", exception.getMessage());
        verify(studentsDAO, times(1)).read(studentId);
        verify(studentsDAO, never()).delete(studentId);
    }

    @Test
    public void deleteStudentById_shouldThrowException_WhenDeleteFails() {

        int studentId = 1;
        Student student = new Student("John", "Doe");
        when(studentsDAO.read(studentId)).thenReturn(Optional.of(student));
        doThrow(new RuntimeException("Database error")).when(studentsDAO).delete(studentId);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.deleteStudentById(studentId));
        assertTrue(exception.getMessage().contains("Failed to delete student"));
        verify(studentsDAO, times(1)).read(studentId);
        verify(studentsDAO, times(1)).delete(studentId);
    }
    
    @Test
    public void addStudentToCourse_shouldAddStudentToCourse_WhenValidInput() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "Math";

        when(studentsDAO.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAO.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAO.isStudentEnrolled(1, 101)).thenReturn(false);

        studentService.addStudentToCourse(studentName, studentLastName, courseName);

        verify(studentsDAO).addCourseToStudent(1, 101);
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenStudentNotFound() {
        String studentName = "NonExistent";
        String studentLastName = "Person";
        String courseName = "Math";

        when(studentsDAO.getStudentIdByName(studentName, studentLastName)).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Student not found: NonExistent Person", exception.getMessage());
        verify(studentsDAO, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenCourseNotFound() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "NonExistentCourse";

        when(studentsDAO.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAO.getCourseIdByName(courseName)).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Course not found: NonExistentCourse", exception.getMessage());
        verify(studentsDAO, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenStudentAlreadyEnrolled() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "Math";

        when(studentsDAO.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAO.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAO.isStudentEnrolled(1, 101)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("The student is already enrolled in the course: Math", exception.getMessage());
        verify(studentsDAO, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotFound() {
        when(studentsDAO.getStudentIdByName("John", "Doe")).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Math")
        );

        assertEquals("Student not found: John Doe", exception.getMessage());
        verify(studentsDAO, times(1)).getStudentIdByName("John", "Doe");
        verifyNoMoreInteractions(courseDAO, studentsDAO);
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenCourseNotFound() {
        when(studentsDAO.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAO.getCourseIdByName("Science")).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Science")
        );

        assertEquals("Course not found: Science", exception.getMessage());
        verify(studentsDAO, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAO, times(1)).getCourseIdByName("Science");
        verifyNoMoreInteractions(courseDAO, studentsDAO);
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotEnrolled() {
        when(studentsDAO.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAO.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAO.isStudentEnrolled(1, 101)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Math")
        );

        assertEquals("The student is not enrolled in the specified course: Math", exception.getMessage());
        verify(studentsDAO, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAO, times(1)).getCourseIdByName("Math");
        verify(courseDAO, times(1)).isStudentEnrolled(1, 101);
        verifyNoMoreInteractions(courseDAO, studentsDAO);
    }
    
    @Test
    public void removeStudentFromCourse_shouldRemoveStudent_WhenValidInput() {
        when(studentsDAO.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAO.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAO.isStudentEnrolled(1, 101)).thenReturn(true);

        studentService.removeStudentFromCourse("John", "Doe", "Math");

        verify(studentsDAO, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAO, times(1)).getCourseIdByName("Math");
        verify(courseDAO, times(1)).isStudentEnrolled(1, 101);
        verify(studentsDAO, times(1)).removeStudentFromCourse(1, 101);
    }
    
}

