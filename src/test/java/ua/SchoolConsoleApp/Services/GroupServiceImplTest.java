package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ua.schoolconsoleapp.dao.GroupDAOld;
import ua.schoolconsoleapp.dao.StudentsDAOld;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.services.GroupServiceImpl;

public class GroupServiceImplTest {

    @Mock
    private GroupDAOld groupDAOld;

    @Mock
    private StudentsDAOld studentsDAOld;

    @InjectMocks
    private GroupServiceImpl groupService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findGroupsWithLessOrEqualStudents_shouldReturnGroups_WhenValidInput() {       
        Group group1 = new Group(1, "Group A");
        Group group2 = new Group(2, "Group B");
        Group group3 = new Group(3, "Group C");

        List<Group> allGroups = Arrays.asList(group1, group2, group3);

        when(groupDAOld.getAll()).thenReturn(allGroups);
        when(studentsDAOld.getNumStudentsInGroup(1)).thenReturn(5);
        when(studentsDAOld.getNumStudentsInGroup(2)).thenReturn(10);
        when(studentsDAOld.getNumStudentsInGroup(3)).thenReturn(15);

        int maxStudents = 10;
        List<Group> result = groupService.findGroupsWithLessOrEqualStudents(maxStudents);

        assertEquals(2, result.size());
        assertTrue(result.contains(group1));
        assertTrue(result.contains(group2));
        assertFalse(result.contains(group3));

        verify(groupDAOld, times(1)).getAll();
        verify(studentsDAOld, times(1)).getNumStudentsInGroup(1);
        verify(studentsDAOld, times(1)).getNumStudentsInGroup(2);
        verify(studentsDAOld, times(1)).getNumStudentsInGroup(3);
    }

    @Test
    public void findGroupsWithLessOrEqualStudents_shouldReturnEmptyList_WhenNoGroupsMatch() {
        Group group1 = new Group(1, "Group A");
        Group group2 = new Group(2, "Group B");

        List<Group> allGroups = Arrays.asList(group1, group2);

        when(groupDAOld.getAll()).thenReturn(allGroups);
        when(studentsDAOld.getNumStudentsInGroup(1)).thenReturn(15);
        when(studentsDAOld.getNumStudentsInGroup(2)).thenReturn(20);

        int maxStudents = 10;
        List<Group> result = groupService.findGroupsWithLessOrEqualStudents(maxStudents);

        assertTrue(result.isEmpty());

        verify(groupDAOld, times(1)).getAll();
        verify(studentsDAOld, times(1)).getNumStudentsInGroup(1);
        verify(studentsDAOld, times(1)).getNumStudentsInGroup(2);
    }

    @Test
    public void findGroupsWithLessOrEqualStudents_shouldThrowException_WhenGroupDAOFails() {
        when(groupDAOld.getAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            groupService.findGroupsWithLessOrEqualStudents(10);
        });

        assertEquals("Failed to find groups with less or equal students", exception.getMessage());
        verify(groupDAOld, times(1)).getAll();
    }

    @Test
    public void findGroupsWithLessOrEqualStudents_shouldHandleEmptyGroupList() {
        when(groupDAOld.getAll()).thenReturn(new ArrayList<>());

        int maxStudents = 10;
        List<Group> result = groupService.findGroupsWithLessOrEqualStudents(maxStudents);

        assertTrue(result.isEmpty());
        verify(groupDAOld, times(1)).getAll();
        verifyNoInteractions(studentsDAOld); 
    }
}
