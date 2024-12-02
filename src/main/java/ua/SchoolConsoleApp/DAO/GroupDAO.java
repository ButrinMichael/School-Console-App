package ua.SchoolConsoleApp.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.SchoolConsoleApp.Group;

@Repository
public class GroupDAO implements Dao<Group> {

	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_GROUP_SQL = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
	private static final String SELECT_GROUP_BY_ID_SQL = "SELECT * FROM school.groups WHERE group_id = ?";
	private static final String UPDATE_GROUPS_SQL = "UPDATE school.groups SET group_name = ? WHERE group_id = ?";
	private static final String UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL = "UPDATE school.students SET group_id = NULL WHERE group_id = ?";
	private static final String DELETE_GROUP_BY_ID_SQL = "DELETE FROM school.groups WHERE group_id = ?";
	private static final String GET_ALL_GROUP_SQL = "SELECT * FROM school.GROUPS";

	@Autowired
	public GroupDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private final RowMapper<Group> groupRowMapper = new RowMapper<Group>() {
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("group_id");
			String name = rs.getString("group_name");
			return new Group(id, name);
		}
	};

	@Override
	public void create(Group group) {
		jdbcTemplate.update(INSERT_GROUP_SQL, group.getName());
	}

	public Optional<Group> read(int id) {
		return jdbcTemplate.query(SELECT_GROUP_BY_ID_SQL, groupRowMapper, id).stream().findFirst();
	}

	@Override
	public void update(Group group) {
		jdbcTemplate.update(UPDATE_GROUPS_SQL, group.getName(), group.getId());
	}

	@Override
	@Transactional
	public void delete(int id) {
		jdbcTemplate.update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, id);
		jdbcTemplate.update(DELETE_GROUP_BY_ID_SQL, id);
	}

	public List<Group> getAll() {
		try {
			return jdbcTemplate.query(GET_ALL_GROUP_SQL, groupRowMapper);
		} catch (DataAccessException e) {
			System.err.println("Error fetching all groups: " + e.getMessage());
			throw new RuntimeException("Failed to fetch groups", e);
		}
	}

}
