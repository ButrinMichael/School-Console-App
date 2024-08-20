package ua.SchoolConsoleApp.DAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
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
    public void create_schouldCreateGroup() throws SQLException {
    
        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(1);

        groupDAO.create(testGroup);
     
        verify(jdbcTemplate, times(1)).update("INSERT INTO school.GROUPS (group_name) VALUES (?)", "Test Group");
    }

	@Test
	public void update_schouldUpdateGroup() throws SQLException {
		groupDAO.update(testGroup);
		verify(jdbcTemplate, times(1)).update("UPDATE school.groups SET group_name = ? WHERE group_id = ?",
				testGroup.getName(), testGroup.getId());
	}

	@Test
	public void delete_schouldDeleteGroup() throws SQLException {
		groupDAO.delete(1);
		verify(jdbcTemplate, times(1)).update("DELETE FROM school.groups WHERE group_id = ?", 1);
		verify(jdbcTemplate, times(1)).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 1);

	}

	@Test
	public void delete_schouldPerformInOrder() throws SQLException {
		groupDAO.delete(1);
		InOrder inOrder = inOrder(jdbcTemplate);
		inOrder.verify(jdbcTemplate).update("UPDATE school.students SET group_id = NULL WHERE group_id = ?", 1);
		inOrder.verify(jdbcTemplate).update("DELETE FROM school.groups WHERE group_id = ?", 1);
	}

	@Test
	public void delete_shouldReturnResult_WithDifferentIdValues() throws SQLException {
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
	public void read_shouldReturnResult_whenGroupFound() throws SQLException {		
		when(jdbcTemplate.query(eq("SELECT * FROM school.groups WHERE group_id = ?"), any(RowMapper.class), eq(1)))
				.thenReturn(Collections.singletonList(testGroup));
		Optional<Group> result = groupDAO.read(1);
		assertTrue(result.isPresent());
		assertEquals("Test Group", result.get().getName());
		verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.groups WHERE group_id = ?"),any(RowMapper.class),eq(1));
	}

	@Test
	public void read_shouldReturnEmtyGroup_whenGroupNotFound() throws SQLException {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenReturn(Collections.emptyList());
	    Optional<Group> result = groupDAO.read(1);
	    assertFalse(result.isPresent());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.groups WHERE group_id = ?"), any(RowMapper.class),eq(1));
	}

	@Test
	public void test_schouldReturnGroupList() {
	    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.singletonList(testGroup));
	    List<Group> result = groupDAO.getAll();
	    assertEquals(1, result.size());
	    assertEquals("Test Group", result.get(0).getName());
	    verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM school.GROUPS"), any(RowMapper.class));
	}
}
