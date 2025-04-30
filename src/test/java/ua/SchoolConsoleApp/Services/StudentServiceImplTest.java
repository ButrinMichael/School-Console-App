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

<<<<<<< HEAD
import ua.schoolconsoleapp.models.Course;
=======
import ua.schoolconsoleapp.dao.CourseDAOld;
import ua.schoolconsoleapp.dao.StudentsDAOld;
>>>>>>> refs/remotes/origin/main
import ua.schoolconsoleapp.models.Student;
import ua.schoolconsoleapp.repositories.CourseRepository;
import ua.schoolconsoleapp.repositories.StudentRepository;
import ua.schoolconsoleapp.services.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

<<<<<<< HEAD
	@Mock
    private StudentRepository studentRepository;
=======
    @Mock
    private CourseDAOld courseDAOld;
>>>>>>> refs/remotes/origin/main

    @Mock
<<<<<<< HEAD
    private CourseRepository courseRepository;
=======
    private StudentsDAOld studentsDAOld;
>>>>>>> refs/remotes/origin/main

    @InjectMocks
    private StudentServiceImpl studentService;

<<<<<<< HEAD
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
=======
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
>>>>>>> refs/remotes/origin/main
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

<<<<<<< HEAD
    @Test
    void getCoursesByStudentName_shouldThrow_whenStudentNotFound() {
    	  when(studentRepository.findWithCoursesByFirstNameAndLastName("X", "Y"))
          .thenReturn(Optional.empty());
    	  
    	  RuntimeException ex = assertThrows(RuntimeException.class,
    	            () -> studentService.getCoursesByStudentName("X", "Y"));

    	        assertEquals("Student not found", ex.getMessage());
    	
=======
        verify(studentsDAOld, times(1)).create(validStudent);
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void findStudentsByCourseName_shouldDelegateToRepository() {
        List<Student> students = List.of(student);
        when(studentRepository.findStudentsByCourseName("Math"))
            .thenReturn(students);

        List<Student> result = studentService.findStudentsByCourseName("Math");

<<<<<<< HEAD
        assertSame(students, result);
        verify(studentRepository).findStudentsByCourseName("Math");
=======
        assertEquals("Name or surname cannot be empty.", exception.getMessage());
        verify(studentsDAOld, never()).create(any());
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void addNewStudent_shouldSave_whenValid() {
        Student s = new Student();
        s.setFirstName("A");
        s.setLastName("B");

        studentService.addNewStudent(s);

<<<<<<< HEAD
        verify(studentRepository).save(s);
=======
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
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void addNewStudent_shouldThrow_whenFirstNameEmpty() {
        Student s = new Student();
        s.setFirstName("");
        s.setLastName("B");

<<<<<<< HEAD
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> studentService.addNewStudent(s));
=======
        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(false);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
        assertEquals("First and last name must not be empty", ex.getMessage());
        verify(studentRepository, never()).save(any());
=======
        studentService.addStudentToCourse(studentName, studentLastName, courseName);

        verify(studentsDAOld).addCourseToStudent(1, 101);
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void deleteStudentById_shouldDelete_whenFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

<<<<<<< HEAD
        studentService.deleteStudentById(1);
=======
        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(-1);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
        verify(studentRepository).delete(student);
=======
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Student not found: NonExistent Person", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void deleteStudentById_shouldThrow_whenNotFound() {
        when(studentRepository.findById(2)).thenReturn(Optional.empty());

<<<<<<< HEAD
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.deleteStudentById(2));
=======
        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(-1);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
        assertEquals("Student not found with ID: 2", ex.getMessage());
        verify(studentRepository, never()).delete(any());
=======
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("Course not found: NonExistentCourse", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
>>>>>>> refs/remotes/origin/main
    }
    
    @Test
    void addStudentToCourse_shouldEnroll_whenHappyPath() {        
        student.getCourses().clear();
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));

<<<<<<< HEAD
        studentService.addStudentToCourse("John", "Doe", "Math");
=======
        when(studentsDAOld.getStudentIdByName(studentName, studentLastName)).thenReturn(1);
        when(courseDAOld.getCourseIdByName(courseName)).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(true);
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
        assertTrue(student.getCourses().contains(course));
        verify(studentRepository).save(student);
=======
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentService.addStudentToCourse(studentName, studentLastName, courseName));

        assertEquals("The student is already enrolled in the course: Math", exception.getMessage());
        verify(studentsDAOld, never()).addCourseToStudent(anyInt(), anyInt());
>>>>>>> refs/remotes/origin/main
    }

    @Test
<<<<<<< HEAD
    void addStudentToCourse_shouldThrow_whenStudentNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("X", "Y"))
            .thenReturn(Optional.empty());
=======
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotFound() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(-1);
>>>>>>> refs/remotes/origin/main

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("X", "Y", "Math"));

<<<<<<< HEAD
        assertEquals("Student not found", ex.getMessage());
        verify(courseRepository, never()).findByName(any());
=======
        assertEquals("Student not found: John Doe", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
>>>>>>> refs/remotes/origin/main
    }

    @Test
<<<<<<< HEAD
    void addStudentToCourse_shouldThrow_whenCourseNotFound() {
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Sci"))
            .thenReturn(Optional.empty());
=======
    public void removeStudentFromCourse_shouldThrowException_WhenCourseNotFound() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Science")).thenReturn(-1);
>>>>>>> refs/remotes/origin/main

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("John", "Doe", "Sci"));

<<<<<<< HEAD
        assertEquals("Course not found", ex.getMessage());
=======
        assertEquals("Course not found: Science", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Science");
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
>>>>>>> refs/remotes/origin/main
    }

    @Test
<<<<<<< HEAD
    void addStudentToCourse_shouldThrow_whenAlreadyEnrolled() {        
        student.getCourses().add(course);
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));
=======
    public void removeStudentFromCourse_shouldThrowException_WhenStudentNotEnrolled() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(false);
>>>>>>> refs/remotes/origin/main

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> studentService.addStudentToCourse("John", "Doe", "Math"));

<<<<<<< HEAD
        assertEquals("Student already enrolled in course", ex.getMessage());
=======
        assertEquals("The student is not enrolled in the specified course: Math", exception.getMessage());
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Math");
        verify(courseDAOld, times(1)).isStudentEnrolled(1, 101);
        verifyNoMoreInteractions(courseDAOld, studentsDAOld);
>>>>>>> refs/remotes/origin/main
    }

    @Test
<<<<<<< HEAD
    void removeStudentFromCourse_shouldRemove_whenCoursePath() {        
        student.getCourses().add(course);
        when(studentRepository.findWithCoursesByFirstNameAndLastName("John", "Doe"))
            .thenReturn(Optional.of(student));
        when(courseRepository.findByName("Math"))
            .thenReturn(Optional.of(course));
=======
    public void removeStudentFromCourse_shouldRemoveStudent_WhenValidInput() {
        when(studentsDAOld.getStudentIdByName("John", "Doe")).thenReturn(1);
        when(courseDAOld.getCourseIdByName("Math")).thenReturn(101);
        when(courseDAOld.isStudentEnrolled(1, 101)).thenReturn(true);
>>>>>>> refs/remotes/origin/main

        studentService.removeStudentFromCourse("John", "Doe", "Math");

<<<<<<< HEAD
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
=======
        verify(studentsDAOld, times(1)).getStudentIdByName("John", "Doe");
        verify(courseDAOld, times(1)).getCourseIdByName("Math");
        verify(courseDAOld, times(1)).isStudentEnrolled(1, 101);
        verify(studentsDAOld, times(1)).removeStudentFromCourse(1, 101);
>>>>>>> refs/remotes/origin/main
    }
    
}

