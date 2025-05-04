package ua.SchoolConsoleApp.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.repositories.GroupRepository;
import ua.schoolconsoleapp.services.GroupServiceImpl;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;


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

    }
    @Test

    void findGroupsWithLessOrEqualStudents_shouldHandleEmptyGroupList() {        
        when(groupRepository.findGroupsWithLessOrEqualStudents(10L))
            .thenReturn(Collections.emptyList());
        
        List<Group> result = groupService.findGroupsWithLessOrEqualStudents(10);
      
        assertTrue(result.isEmpty(), "If there are no groups, the service is expected to return an empty list.");
        verify(groupRepository).findGroupsWithLessOrEqualStudents(10L);

    }
}
