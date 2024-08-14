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

	private static final String InsertGroupSQL = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
	private static final String SelectGroupByIdSQL = "SELECT * FROM school.groups WHERE group_id = ?";;
	private static final String UpdateGroupsSQL = "UPDATE school.groups SET group_name = ? WHERE group_id = ?";;
	private static final String UpdateStudentsGroupByGroupIdSQL = "UPDATE school.students SET group_id = NULL WHERE group_id = ?";;
	private static final String DeleteGroupByIdSQL = "DELETE FROM school.groups WHERE group_id = ?";;
	private static final String GetAllGroupsSQL = "SELECT * FROM school.GROUPS";;

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
	public void create(Group group) throws SQLException {
		jdbcTemplate.update(InsertGroupSQL, group.getName());
	}

	public Optional<Group> read(int id) throws SQLException {
		return jdbcTemplate.query(SelectGroupByIdSQL, groupRowMapper, id).stream().findFirst();
	}

	@Override
	public void update(Group group) throws SQLException {
		jdbcTemplate.update(UpdateGroupsSQL, group.getName(), group.getId());
	}

	@Override
	@Transactional
	public void delete(int id) throws SQLException {
		jdbcTemplate.update(UpdateStudentsGroupByGroupIdSQL, id);
		jdbcTemplate.update(DeleteGroupByIdSQL, id);
	}

	public List<Group> getAll() {
		try {
			return jdbcTemplate.query(GetAllGroupsSQL, groupRowMapper);
		} catch (DataAccessException e) {
			System.err.println("Error fetching all groups: " + e.getMessage());
			throw new RuntimeException("Failed to fetch groups", e);
		}
	}
}
