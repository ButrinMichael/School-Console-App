package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.repositories.CourseRepository;
import ua.schoolconsoleapp.services.CourseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

	@Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        course1 = new Course();
        course1.setId(1);
        course1.setName("Math");
        course2 = new Course();
        course2.setId(2);
        course2.setName("History");
    }

    @Test
    void getAllCourses_shouldReturnList() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        List<Course> result = courseService.getAllCourses();

        assertEquals(2, result.size());
        assertTrue(result.contains(course1));
        assertTrue(result.contains(course2));
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getAllCourses_shouldReturnEmptyList_whenNoCourses() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        List<Course> result = courseService.getAllCourses();

        assertTrue(result.isEmpty());
        verify(courseRepository).findAll();
    }

    @Test
    void findByName_shouldReturnCourse_whenFound() {
        when(courseRepository.findByName("Math")).thenReturn(Optional.of(course1));

        Optional<Course> result = courseService.findByName("Math");

        assertTrue(result.isPresent());
        assertEquals(course1, result.get());
        verify(courseRepository).findByName("Math");
    }

    @Test
    void findByName_shouldReturnEmptyOptional_whenNotFound() {
        when(courseRepository.findByName("Philosophy")).thenReturn(Optional.empty());

        Optional<Course> result = courseService.findByName("Philosophy");

        assertFalse(result.isPresent());
        verify(courseRepository).findByName("Philosophy");
    }

    @Test
    void findCoursesByStudentId_shouldReturnList() {
        when(courseRepository.findCoursesByStudentId(42))
            .thenReturn(Arrays.asList(course1, course2));

        List<Course> result = courseService.findCoursesByStudentId(42);

        assertEquals(2, result.size());
        verify(courseRepository).findCoursesByStudentId(42);
    }

    @Test
    void findCoursesByStudentId_shouldReturnEmptyList_whenNone() {
        when(courseRepository.findCoursesByStudentId(99))
            .thenReturn(Collections.emptyList());

        List<Course> result = courseService.findCoursesByStudentId(99);

        assertTrue(result.isEmpty());
        verify(courseRepository).findCoursesByStudentId(99);
    }

    @Test
    void findCoursesByStudentId_shouldPropagateException() {
        when(courseRepository.findCoursesByStudentId(anyInt()))
            .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            courseService.findCoursesByStudentId(1)
        );
        assertEquals("DB error", ex.getMessage());
    }

}
