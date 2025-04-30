package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
<<<<<<< HEAD
import org.mockito.junit.jupiter.MockitoExtension;
=======
import org.mockito.MockitoAnnotations;

import ua.schoolconsoleapp.dao.GroupDAOld;
import ua.schoolconsoleapp.dao.StudentsDAOld;
>>>>>>> refs/remotes/origin/main
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.repositories.GroupRepository;
import ua.schoolconsoleapp.services.GroupServiceImpl;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
<<<<<<< HEAD
    private GroupRepository groupRepository;
=======
    private GroupDAOld groupDAOld;

    @Mock
    private StudentsDAOld studentsDAOld;
>>>>>>> refs/remotes/origin/main

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void findGroupsWithLessOrEqualStudents_ShouldReturnGroups() {        
        Group g1 = new Group();
        g1.setId(1);
        g1.setName("G1");
        Group g2 = new Group();
        g2.setId(2);
        g2.setName("G2");
        List<Group> expected = List.of(g1, g2);

        when(groupRepository.findGroupsWithLessOrEqualStudents(5L))
            .thenReturn(expected);

       List<Group> actual = groupService.findGroupsWithLessOrEqualStudents(5);

        assertEquals(expected, actual, "The service should return the same list that the repository gave.");
        verify(groupRepository).findGroupsWithLessOrEqualStudents(5L);
    }

    @Test
<<<<<<< HEAD
    void findGroupsWithLessOrEqualStudents_RepositoryThrows_ShouldWrapAndRethrow() {        
        when(groupRepository.findGroupsWithLessOrEqualStudents(anyLong()))
            .thenThrow(new RuntimeException("DB error"));
        
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            groupService.findGroupsWithLessOrEqualStudents(10)
        );
        assertEquals(
            "Failed to find groups with less or equal students",
            ex.getMessage(),
            "Failed to find groups with less or equal students"
        );        
        assertNotNull(ex.getCause());
        assertEquals("DB error", ex.getCause().getMessage());
=======
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
>>>>>>> refs/remotes/origin/main
    }
    @Test
<<<<<<< HEAD
    void findGroupsWithLessOrEqualStudents_shouldHandleEmptyGroupList() {        
        when(groupRepository.findGroupsWithLessOrEqualStudents(10L))
            .thenReturn(Collections.emptyList());
        
        List<Group> result = groupService.findGroupsWithLessOrEqualStudents(10);
      
        assertTrue(result.isEmpty(), "If there are no groups, the service is expected to return an empty list.");
        verify(groupRepository).findGroupsWithLessOrEqualStudents(10L);
=======
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
>>>>>>> refs/remotes/origin/main
    }
}
