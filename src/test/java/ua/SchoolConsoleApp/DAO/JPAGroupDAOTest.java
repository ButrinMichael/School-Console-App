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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ua.schoolconsoleapp.dao.JPAGroupDAO;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.models.Student;

@ExtendWith(MockitoExtension.class)
public class JPAGroupDAOTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private JPAGroupDAO groupDAO;

	private Group testGroup;

	@BeforeEach
	public void setup() {
		testGroup = new Group(1, "Test Group");
	}

	@Test
	public void create_shouldPersistGroup() {
		groupDAO.create(testGroup);
		verify(entityManager, times(1)).persist(testGroup);
	}

	@Test
	public void create_shouldThrowRuntimeException() {
		doThrow(new RuntimeException("Hibernate persist error")).when(entityManager).persist(testGroup);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			groupDAO.create(testGroup);
		});

		assertEquals("Hibernate persist error", exception.getMessage());
		verify(entityManager).persist(testGroup);
	}

	@Test
	public void read_shouldReturnResult_whenGroupFound() {

		Group testGroup = new Group();
		testGroup.setId(1);
		testGroup.setName("Test Group");

		when(entityManager.find(Group.class, 1)).thenReturn(testGroup);

		Optional<Group> result = groupDAO.read(1);

		assertTrue(result.isPresent(), "Expected a group to be present in the Optional");
		assertEquals(1, result.get().getId());
		assertEquals("Test Group", result.get().getName());

		verify(entityManager).find(Group.class, 1);
	}

	@Test
	public void read_shouldThrowException_whenEntityManagerFails() {	    
	    when(entityManager.find(Group.class, 1))
	            .thenThrow(new IllegalArgumentException("Invalid ID"));

	   	    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	        groupDAO.read(1);
	    });

	    assertEquals("Invalid ID", exception.getMessage());
	    verify(entityManager).find(Group.class, 1);
	}

	@Test
	public void read_shouldReturnEmpty_whenGroupNotFound() {
	    when(entityManager.find(Group.class, 1)).thenReturn(null);
	    
	    Optional<Group> result = groupDAO.read(1);

	    assertFalse(result.isPresent(), "Expected Optional to be empty when group not found");
	    verify(entityManager).find(Group.class, 1);
	}

	@Test
	public void update_shouldMergeGroup() {
		groupDAO.update(testGroup);
		verify(entityManager, times(1)).merge(testGroup);
	}

	@Test
	public void update_schouldThrowNullPointerException_whenGroupisNull() {
		assertThrows(NullPointerException.class, () -> {
			groupDAO.update(null);
		});
	}

	@Test
	public void update_shouldThrowDataAccessException() {
		doThrow(new IllegalArgumentException("Merge failed")).when(entityManager).merge(testGroup);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			groupDAO.update(testGroup);
		});

		assertEquals("Merge failed", exception.getMessage());
		verify(entityManager, times(1)).merge(testGroup);
	}

	@Test
	public void delete_schouldDeleteGroup() {

		Group group = mock(Group.class);
		Student student1 = mock(Student.class);
		Student student2 = mock(Student.class);
		List<Student> students = List.of(student1, student2);

		when(entityManager.find(Group.class, 1)).thenReturn(group);
		when(group.getStudents()).thenReturn(students);

		groupDAO.delete(1);

		verify(entityManager).find(Group.class, 1);
		verify(student1).setGroup(null);
		verify(student2).setGroup(null);
		verify(entityManager).remove(group);

	}

	@Test
	public void delete_schouldDoNothing_whenGroupWithNonExistingId() {
		int nonExistentId = 999;
		when(entityManager.find(Group.class, nonExistentId)).thenReturn(null);
		groupDAO.delete(nonExistentId);
		verify(entityManager, never()).remove(any(Group.class));
	}

	@Test
	public void delete_shouldPerformInOrder() {
		Group mockGroup = new Group();
		mockGroup.setId(1);
		Student mockStudent1 = new Student();
		Student mockStudent2 = new Student();
		mockGroup.setStudents(List.of(mockStudent1, mockStudent2));

		when(entityManager.find(Group.class, 1)).thenReturn(mockGroup);

		groupDAO.delete(1);

		InOrder inOrder = inOrder(entityManager);

		inOrder.verify(entityManager).find(Group.class, 1);

		inOrder.verify(entityManager).remove(mockGroup);

		assertNull(mockStudent1.getGroup());
		assertNull(mockStudent2.getGroup());
	}

	@Test
	public void delete_shouldThrowDataAccessException() {
		Group dummyGroup = new Group();
		dummyGroup.setId(1);
		dummyGroup.setName("Test Group");

		when(entityManager.find(Group.class, 1)).thenReturn(dummyGroup);

		doThrow(new DataAccessException("Database error") {
		}).when(entityManager).remove(dummyGroup);

		DataAccessException exception = assertThrows(DataAccessException.class, () -> {
			groupDAO.delete(1);
		});

		assertEquals("Database error", exception.getMessage());
	}

	@Test
	public void delete_shouldDeleteGroup_WithPositiveId() {
		Group group = new Group();
		group.setId(1);

		Student student1 = mock(Student.class);
		Student student2 = mock(Student.class);
		group.setStudents(List.of(student1, student2));

		when(entityManager.find(Group.class, 1)).thenReturn(group);

		groupDAO.delete(1);

		verify(entityManager).find(Group.class, 1);
		verify(student1).setGroup(null);
		verify(student2).setGroup(null);
		verify(entityManager).remove(group);
	}

	@Test
	public void delete_shouldDeleteGroup_WithZeroId() {
		when(entityManager.find(Group.class, 0)).thenReturn(null);
		groupDAO.delete(0);
		verify(entityManager).find(Group.class, 0);
	    verify(entityManager, never()).remove(any());
	}

	@Test
	public void delete_shouldDeleteGroup_WithNegativeId() {
		when(entityManager.find(Group.class, -1)).thenReturn(null);
		groupDAO.delete(-1);
		verify(entityManager, times(1)).find(Group.class, -1);
	    verify(entityManager, never()).remove(any());
	}

	@Test
	public void getAll_shouldReturnGroupList() {
		List<Group> mockGroups = List.of(testGroup);
		TypedQuery<Group> mockQuery = mock(TypedQuery.class);

		when(entityManager.createQuery("SELECT g FROM Group g", Group.class)).thenReturn(mockQuery);
		when(mockQuery.getResultList()).thenReturn(mockGroups);

		List<Group> result = groupDAO.getAll();

		assertEquals(1, result.size());
		assertEquals("Test Group", result.get(0).getName());

		verify(entityManager, times(1)).createQuery("SELECT g FROM Group g", Group.class);
		verify(mockQuery, times(1)).getResultList();
	}

	@Test
	public void getAll_shouldThrowRuntimeException() {
	    when(entityManager.createQuery("SELECT g FROM Group g", Group.class))
	            .thenThrow(new RuntimeException("Failed to fetch groups"));

	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.getAll();
	    });

	    assertEquals("Failed to fetch groups", exception.getMessage());
	    verify(entityManager).createQuery("SELECT g FROM Group g", Group.class);
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnList() {
		TypedQuery<Group> mockQuery = mock(TypedQuery.class);
		List<Group> expectedGroups = List.of(new Group(1, "G1"), new Group(2, "G2"));

		when(entityManager.createQuery(anyString(), eq(Group.class))).thenReturn(mockQuery);
		when(mockQuery.setParameter(eq("maxStudents"), anyLong())).thenReturn(mockQuery);
		when(mockQuery.getResultList()).thenReturn(expectedGroups);

		List<Group> result = groupDAO.findGroupsWithLessOrEqualStudents(30);

		assertEquals(expectedGroups.size(), result.size());
		verify(entityManager).createQuery(contains("HAVING COUNT"), eq(Group.class));
		verify(mockQuery).setParameter("maxStudents", 30L);
		verify(mockQuery).getResultList();
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldReturnEmptyList() {
		TypedQuery<Group> mockQuery = mock(TypedQuery.class);

		when(entityManager.createQuery(anyString(), eq(Group.class))).thenReturn(mockQuery);
		when(mockQuery.setParameter(eq("maxStudents"), anyLong())).thenReturn(mockQuery);
		when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

		List<Group> result = groupDAO.findGroupsWithLessOrEqualStudents(10);

		assertTrue(result.isEmpty());
		verify(mockQuery).getResultList();
	}

	@Test
	public void findGroupsWithLessOrEqualStudents_shouldThrowRuntimeException() {
	    when(entityManager.createQuery(anyString(), eq(Group.class)))
	            .thenThrow(new RuntimeException("Query error"));

	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        groupDAO.findGroupsWithLessOrEqualStudents(10);
	    });

	    assertEquals("Query error", exception.getMessage());
	}
}
