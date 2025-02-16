package ua.SchoolConsoleApp.DAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ua.schoolconsoleapp.Group;
import ua.schoolconsoleapp.dao.GroupDAO;

@ExtendWith(MockitoExtension.class)
public class GroupDAOTest {
	private static final String INSERT_GROUP_SQL = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
	private static final String SELECT_GROUP_BY_ID_SQL = "SELECT * FROM school.groups WHERE group_id = ?";
	private static final String UPDATE_GROUPS_SQL = "UPDATE school.groups SET group_name = ? WHERE group_id = ?";
	private static final String UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL = "UPDATE school.students SET group_id = NULL WHERE group_id = ?";
	private static final String DELETE_GROUP_BY_ID_SQL = "DELETE FROM school.groups WHERE group_id = ?";
	private static final String GET_ALL_GROUP_SQL = "SELECT * FROM school.GROUPS";
	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private GroupDAO groupDAO;

	private Group testGroup;

	@BeforeEach
	public void setup() {
		testGroup = new Group(1, "Test Group");
	}

	@Test
    public void create_schouldCreateGroup() {
    
        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(1);

        groupDAO.create(testGroup);
     
        verify(jdbcTemplate, times(1)).update(INSERT_GROUP_SQL, "Test Group");
    }

	@Test
	public void create_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.update(anyString(), anyString()))
	            .thenThrow(new DataAccessException("Database error") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	        groupDAO.create(testGroup);
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq(INSERT_GROUP_SQL), eq(testGroup.getName()));
	}

	@Test
	public void read_shouldReturnResult_whenGroupFound() {
	    // Arrange
	    when(jdbcTemplate.query(eq(SELECT_GROUP_BY_ID_SQL), any(RowMapper.class), eq(1)))
	            .thenReturn(Collections.singletonList(testGroup));
	    Optional<Group> result = groupDAO.read(1);
	    assertTrue(result.isPresent(), "Expected a group to be present in the Optional");
	    Group actualGroup = result.get();
	    assertEquals(testGroup.getId(), actualGroup.getId(), "Group IDs should match");
	    assertEquals(testGroup.getName(), actualGroup.getName(), "Group names should match");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_GROUP_BY_ID_SQL), any(RowMapper.class), eq(1));
	}
	
	@Test
	public void read_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
	            .thenThrow(new DataAccessException("Failed to read group") {});    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	        groupDAO.read(1);
	    });
	    assertEquals("Failed to read group", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_GROUP_BY_ID_SQL), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnEmtyGroup_whenGroupNotFound() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Group> result = groupDAO.read(1);
	    assertFalse(result.isPresent(), "Expected Optional to be empty when no group is found");
	    verify(jdbcTemplate, times(1)).query(eq(SELECT_GROUP_BY_ID_SQL), any(RowMapper.class),eq(1));
	}


	@Test
	public void update_schouldUpdateGroup() {
		groupDAO.update(testGroup);
		verify(jdbcTemplate, times(1)).update(UPDATE_GROUPS_SQL,
				testGroup.getName(), testGroup.getId());
	}

	@Test
	public void update_schouldThrowNullPointerException_whenGroupisNull() {
		assertThrows(NullPointerException.class, () -> {
			groupDAO.update(null);
		});
	}

	@Test
	public void update_shouldReturnDataAccessException() {
	    when(jdbcTemplate.update(anyString(), anyString(), anyInt())).thenThrow(new DataAccessException("Database error") {});
	    
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	        groupDAO.update(testGroup);
	    });	
	    assertEquals("Database error",  exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq(UPDATE_GROUPS_SQL),eq(testGroup.getName()), eq(testGroup.getId()));
		}


	@Test
	public void delete_schouldDeleteGroup() {
		groupDAO.delete(1);
		verify(jdbcTemplate, times(1)).update(DELETE_GROUP_BY_ID_SQL, 1);
		verify(jdbcTemplate, times(1)).update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, 1);

	}

	@Test
	public void delete_schouldDoNothing_whenGroupWithNonExistingId() {	    
	    when(jdbcTemplate.update(anyString(), eq(999999999))).thenReturn(0);
	    groupDAO.delete(999999999);
	    verify(jdbcTemplate, times(1)).update(eq(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL), eq(999999999));
	    verify(jdbcTemplate, times(1)).update(eq(DELETE_GROUP_BY_ID_SQL), eq(999999999));
	}

	@Test
	public void delete_schouldPerformInOrder() {
		groupDAO.delete(1);
		InOrder inOrder = inOrder(jdbcTemplate);
		inOrder.verify(jdbcTemplate).update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, 1);
		inOrder.verify(jdbcTemplate).update(DELETE_GROUP_BY_ID_SQL, 1);
	}

	@Test
	public void delete_shouldThrowDataAccessException() {	   
	    when(jdbcTemplate.update(eq(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL), eq(1)))
	            .thenThrow(new DataAccessException("Database error") {});
	    DataAccessException exception = assertThrows(DataAccessException.class, () -> {
	        groupDAO.delete(1);
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL), eq(1));
	    verify(jdbcTemplate, times(0)).update(eq(DELETE_GROUP_BY_ID_SQL), eq(1));
	}

	@Test
	public void delete_shouldDeleteCourse_WithPositiveId() {
		groupDAO.delete(1);
		verify(jdbcTemplate).update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, 1);
		verify(jdbcTemplate).update(DELETE_GROUP_BY_ID_SQL, 1);
	}

	@Test
	public void delete_shouldDeleteCourse_WithZeroId() {
		groupDAO.delete(0);
		verify(jdbcTemplate).update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, 0);
		verify(jdbcTemplate).update(DELETE_GROUP_BY_ID_SQL, 0);
	}

	@Test
	public void delete_shouldDeleteCourse_WithNegativeId() {
		groupDAO.delete(-1);
		verify(jdbcTemplate).update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, -1);
		verify(jdbcTemplate).update(DELETE_GROUP_BY_ID_SQL, -1);
	}
	@Test
	public void getAll_schouldReturnGroupList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testGroup));
	    List<Group> result = groupDAO.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test Group", result.get(0).getName());
	    verify(jdbcTemplate, times(1)).query(eq(GET_ALL_GROUP_SQL), any(RowMapper.class));
	}

	@Test
	public void getAll_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
	            .thenThrow(new DataAccessException("Failed to fetch groups") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.getAll();
	    });
	    assertEquals("Failed to fetch groups", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq(GET_ALL_GROUP_SQL), any(RowMapper.class));
	}
}
