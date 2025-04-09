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

import ua.schoolconsoleapp.dao.CourseDAOld;
import ua.schoolconsoleapp.dao.StudentsDAOld;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.services.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private CourseDAOld courseDAOld;

    @Mock
    private StudentsDAOld studentsDAOld;

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

        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(courseId);
        when(studentsDAOld.getStudentsByCourseName(courseName)).thenReturn(students);

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertEquals(2, result.size());
        assertEquals(students, result);
        verify(courseDAOld, times(1)).getCourseIdByName(courseName);
        verify(studentsDAOld, times(1)).getStudentsByCourseName(courseName);
    }
    @Test
    public void findStudentsByCourseName_shouldReturnEmptyList_WhenCourseNotFound() {
        String courseName = "UnknownCourse";

        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(-1);

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertTrue(result.isEmpty());
        verify(courseDAOld, times(1)).getCourseIdByName(courseName);
        verify(studentsDAOld, never()).getStudentsByCourseName(anyString());
    }

  
    @Test
    public void findStudentsByCourseName_shouldReturnEmptyList_WhenNoStudentsOnCourse() {
        String courseName = "Math";
        int courseId = 1;

        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(courseId);
        when(studentsDAOld.getStudentsByCourseName(courseName)).thenReturn(List.of());

        List<Student> result = studentService.findStudentsByCourseName(courseName);

        assertTrue(result.isEmpty());
        verify(courseDAOld, times(1)).getCourseIdByName(courseName);
        verify(studentsDAOld, times(1)).getStudentsByCourseName(courseName);
    }
    
    @Test
    public void addNewStudent_shouldCallDAOCreate_WhenStudentIsValid() {
        Student validStudent = new Student("John", "Doe");

        studentService.addNewStudent(validStudent);

        verify(studentsDAOld, times(1)).create(validStudent);
    }
    
    @Test
    public void addNewStudent_shouldThrowException_WhenFirstNameIsEmpty() {
        Student invalidStudent = new Student("", "Doe");

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> studentService.addNewStudent(invalidStudent));

        assertEquals("Name or surname cannot be empty.", exception.getMessage());
        verify(studentsDAOld, never()).create(any());
    }
    
    @Test
    public void addNewStudent_shouldThrowException_WhenLastNameIsEmpty() {
        Student invalidStudent = new Student("John", "");

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> studentService.addNewStudent(invalidStudent));

        assertEquals("Name or surname cannot be empty.", exception.getMessage());
        verify(studentsDAOld, never()).create(any());
    }
    @Test
    public void deleteStudentById_shouldDeleteStudent_WhenValidId() {

        int studentId = 1;
        Student student = new Student("John", "Doe");
        when(studentsDAOld.read(studentId)).thenReturn(Optional.of(student));
        doNothing().when(studentsDAOld).delete(studentId);


        studentService.deleteStudentById(studentId);


        verify(studentsDAOld, times(1)).read(studentId);
        verify(studentsDAOld, times(1)).delete(studentId);
    }

    @Test
    public void deleteStudentById_shouldThrowException_WhenStudentDoesNotExist() {

        int studentId = 1;
        when(studentsDAOld.read(studentId)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.deleteStudentById(studentId));
        assertEquals("Failed to delete student: Student with ID 1 does not exist.", exception.getMessage());
        verify(studentsDAOld, times(1)).read(studentId);
        verify(studentsDAOld, never()).delete(studentId);
    }

    @Test
    public void deleteStudentById_shouldThrowException_WhenDeleteFails() {

        int studentId = 1;
        Student student = new Student("John", "Doe");
        when(studentsDAOld.read(studentId)).thenReturn(Optional.of(student));
        doThrow(new RuntimeException("Database error")).when(studentsDAOld).delete(studentId);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.deleteStudentById(studentId));
        assertTrue(exception.getMessage().contains("Failed to delete student"));
        verify(studentsDAOld, times(1)).read(studentId);
        verify(studentsDAOld, times(1)).delete(studentId);
    }
    
    @Test
    public void addStudentToCourse_shouldAddStudentToCourse_WhenValidInput() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "Math";

        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(false);

        studentService.addStudentToCourse(studentName, studentLastName, courseName);

        verify(studentsDAOld).addCourseToStudent(1, 101);
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenStudentNotFound() {
        String studentName = "NonExistent";
        String studentLastName = "Person";
        String courseName = "Math";

        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Student not found: NonExistent Person", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenCourseNotFound() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "NonExistentCourse";

        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Course not found: NonExistentCourse", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void addStudentToCourse_shouldThrowException_WhenStudentAlreadyEnrolled() {
        String studentName = "John";
        String studentLastName = "Doe";
        String courseName = "Math";

        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("The student is already enrolled in the course: Math", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotFound() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Math")
        );

        assertEquals("Student not found: John Doe", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenCourseNotFound() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Science")).thenReturn(-1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Science")
        );

        assertEquals("Course not found: Science", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Science");
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
    }
    
    @Test
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotEnrolled() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            studentService.removeStudentFromCourse("John", "Doe", "Math")
        );

        assertEquals("The student is not enrolled in the specified course: Math", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Math");
        verify(courseDAOld, times(1)).isStudentEnrolled(1, 101);
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
    }
    
    @Test
    public void removeStudentFromCourse_shouldRemoveStudent_WhenValidInput() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(true);

        studentService.removeStudentFromCourse("John", "Doe", "Math");

        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Math");
        verify(courseDAOld, times(1)).isStudentEnrolled(1, 101);
        verify(studentsDAOld, times(1)).removeStudentFromCourse(1, 101);
    }
    
}

