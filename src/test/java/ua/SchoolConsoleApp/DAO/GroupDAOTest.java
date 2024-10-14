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
import ua.SchoolConsoleApp.Group;

@ExtendWith(MockitoExtension.class)
public class GroupDAOTest {

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
     
        verify(jdbcTemplate, times(1)).update("INSERT INTO school.GROUPS (group_name) VALUES (?)", "Test Group");
    }

	@Test
	public void create_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.update(anyString(), anyString()))
	            .thenThrow(new DataAccessException("Database error") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.create(testGroup);
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq("INSERT INTO school.GROUPS (group_name) VALUES (?)"), eq(testGroup.getName()));
	}

	@Test
	public void read_shouldReturnResult_whenGroupFound(){		
		when(jdbcTemplate.query(eq("SELECT * FROM school.groups WHERE group_id = ?"), any(RowMapper.class), eq(1)))
				.thenReturn(Collections.singletonList(testGroup));
		Optional<Group> result = groupDAO.read(1);
		assertTrue(result.isPresent());
		assertEquals("Test Group", result.get().getName());
		verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.groups WHERE group_id = ?"),any(RowMapper.class),eq(1));
	}

	@Test
	public void read_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt()))
	            .thenThrow(new DataAccessException("Failed to read group") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.read(1);
	    });
	    assertEquals("Failed to read group", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.groups WHERE group_id = ?"), any(RowMapper.class), eq(1));
	}

	@Test
	public void read_shouldReturnEmtyGroup_whenGroupNotFound() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Group> result = groupDAO.read(1);
	    assertFalse(result.isPresent());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.groups WHERE group_id = ?"), any(RowMapper.class),eq(1));
	}

	@Test
	public void update_schouldUpdateGroup() {
		groupDAO.update(testGroup);
		verify(jdbcTemplate, times(1)).update("UPDATE school.groups SET group_name = ? WHERE group_id = ?",
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
	    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.update(testGroup);
	    });	
	    
	    assertEquals("Database error",  exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq("UPDATE school.groups SET group_name = ? WHERE group_id = ?"),eq(testGroup.getName()), eq(testGroup.getId()));
		}

	@Test
	public void delete_schouldDeleteGroup() {
		groupDAO.delete(1);
		verify(jdbcTemplate, times(1)).update("DELETE FROM school.groups WHERE group_id = ?", 1);
		verify(jdbcTemplate, times(1)).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 1);

	}

	@Test
	public void delete_schouldDoNothing_whenGroupWithNonExistingId() {	    
	    when(jdbcTemplate.update(anyString(), eq(999999999))).thenReturn(0);
	    groupDAO.delete(999999999);
	    verify(jdbcTemplate, times(1)).update(eq("UPDATE school.students SET group_id = NULL WHERE group_id = ?"), eq(999999999));
	    verify(jdbcTemplate, times(1)).update(eq("DELETE FROM school.groups WHERE group_id = ?"), eq(999999999));
	}

	@Test
	public void delete_schouldPerformInOrder() {
		groupDAO.delete(1);
		InOrder inOrder = inOrder(jdbcTemplate);
		inOrder.verify(jdbcTemplate).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 1);
		inOrder.verify(jdbcTemplate).update("DELETE FROM school.groups WHERE group_id = ?", 1);
	}

	@Test
	public void delete_shouldThrowDataAccessException() {	   
	    when(jdbcTemplate.update(eq("UPDATE school.students SET group_id = NULL WHERE group_id = ?"), eq(1)))
	            .thenThrow(new DataAccessException("Database error") {});
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.delete(1);
	    });
	    assertEquals("Database error", exception.getMessage());
	    verify(jdbcTemplate, times(1)).update(eq("UPDATE school.students SET group_id = NULL WHERE group_id = ?"), eq(1));
	    verify(jdbcTemplate, times(0)).update(eq("DELETE FROM school.groups WHERE group_id = ?"), eq(1));
	}

	@Test
	public void delete_shouldReturnResult_WithDifferentIdValues() {
		groupDAO.delete(1);
		verify(jdbcTemplate).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 1);
		verify(jdbcTemplate).update("DELETE FROM school.groups WHERE group_id = ?", 1);
		groupDAO.delete(0);
		verify(jdbcTemplate).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 0);
		verify(jdbcTemplate).update("DELETE FROM school.groups WHERE group_id = ?", 0);
		groupDAO.delete(-1);
		verify(jdbcTemplate).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", -1);
		verify(jdbcTemplate).update("DELETE FROM school.groups WHERE group_id = ?", -1);
	}

	@Test
	public void getAll_schouldReturnGroupList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testGroup));
	    List<Group> result = groupDAO.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test Group", result.get(0).getName());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.GROUPS"), any(RowMapper.class));
	}

	@Test
	public void getAll_shouldReturnDataAccessException(){	    
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
	            .thenThrow(new DataAccessException("Failed to fetch groups") {});    
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.getAll();
	    });
	    assertEquals("Failed to fetch groups", exception.getMessage());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.GROUPS"), any(RowMapper.class));
	}
}
