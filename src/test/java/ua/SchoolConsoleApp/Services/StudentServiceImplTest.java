package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.repositories.CourseRepository;
import ua.schoolconsoleapp.repositories.StudentRepository;
import ua.schoolconsoleapp.services.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

	@Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private Course course;
    
    @BeforeEach
    void setUp() {    	
        student = new Student();
        student.setId(1);
        student.setFirstName("John");
        student.setLastName("Doe");
        course = new Course();
        course.setId(10);
        course.setName("Math");
    }
    
    @Test
    void getCoursesByStudentName_shouldReturnCourses_whenStudentExists() {        
        student.getCourses().add(course);
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        
        List<Course> result = studentService.getCoursesByStudentName("John", "Doe");

        
        assertEquals(1, result.size());
        assertSame(course, result.get(0));
    }

    @Test
    void getCoursesByStudentName_shouldThrow_whenStudentNotFound() {
    	  when(studentRepository.findWithCoursesByFirstNameAndLastName("X", "Y"))
          .thenReturn(Optional.empty());
    	  
    	  RuntimeException ex = assertThrows(RuntimeException.class,
    	            () -> studentService.getCoursesByStudentName("X", "Y"));

    	        assertEquals("Student not found", ex.getMessage());
    	
    }
    
    @Test
    void findStudentsByCourseName_shouldDelegateToRepository() {
        List<Student> students = List.of(student);
        when(studentRepository.findStudentsByCourseName("Math"))
            .thenReturn(students);

        List<Student> result = studentService.findStudentsByCourseName("Math");

        assertSame(students, result);
        verify(studentRepository).findStudentsByCourseName("Math");
    }
    
    @Test
    void addNewStudent_shouldSave_whenValid() {
        Student s = new Student();
        s.setFirstName("A");
        s.setLastName("B");

        studentService.addNewStudent(s);

        verify(studentRepository).save(s);
    }
    
    @Test
    void addNewStudent_shouldThrow_whenFirstNameEmpty() {
        Student s = new Student();
        s.setFirstName("");
        s.setLastName("B");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> studentService.addNewStudent(s));

        assertEquals("First and last name must not be empty", ex.getMessage());
        verify(studentRepository, never()).save(any());
    }
    
    @Test
    void deleteStudentById_shouldDelete_whenFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        studentService.deleteStudentById(1);

        verify(studentRepository).delete(student);
    }
    
    @Test
    void deleteStudentById_shouldThrow_whenNotFound() {
        when(studentRepository.findById(2)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.deleteStudentById(2));

        assertEquals("Student not found with ID: 2", ex.getMessage());
        verify(studentRepository, never()).delete(any());
    }
    
    @Test
    void addStudentToCourse_shouldEnroll_whenHappyPath() {        
        student.getCourses().clear();
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));

        studentService.addStudentToCourse("John", "Doe", "Math");

        assertTrue(student.getCourses().contains(course));
        verify(studentRepository).save(student);
    }

    @Test
    void addStudentToCourse_shouldThrow_whenStudentNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("X", "Y"))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("X", "Y", "Math"));

        assertEquals("Student not found", ex.getMessage());
        verify(courseRepository, never()).findByName(any());
    }

    @Test
    void addStudentToCourse_shouldThrow_whenCourseNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Sci"))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("John", "Doe", "Sci"));

        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    void addStudentToCourse_shouldThrow_whenAlreadyEnrolled() {        
        student.getCourses().add(course);
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("John", "Doe", "Math"));

        assertEquals("Student already enrolled in course", ex.getMessage());
    }

    @Test
    void removeStudentFromCourse_shouldRemove_whenCoursePath() {        
        student.getCourses().add(course);
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));

        studentService.removeStudentFromCourse("John", "Doe", "Math");

        assertFalse(student.getCourses().contains(course));
        verify(studentRepository).save(student);
    }

    @Test
    void removeStudentFromCourse_shouldThrow_whenStudentNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("X", "Y"))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.removeStudentFromCourse("X", "Y", "Math"));

        assertEquals("Student not found", ex.getMessage());
    }

    @Test
    void removeStudentFromCourse_shouldThrow_whenCourseNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Sci"))
           .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.removeStudentFromCourse("John", "Doe", "Sci"));

        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    void removeStudentFromCourse_shouldThrow_whenNotEnrolled() {        
        student.getCourses().clear();
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.removeStudentFromCourse("John", "Doe", "Math"));

        assertEquals("Student is not enrolled in course", ex.getMessage());
    }
    
}

