package ua.SchoolConsoleApp.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ua.SchoolConsoleApp.Group;

@Repository
public class GroupDAO implements Dao<Group> {

	private final JdbcTemplate jdbcTemplate;

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
		String sql = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
		jdbcTemplate.update(sql, group.getName());
	}

	@Override
	public Group read(int id) throws SQLException {
		String sql = "SELECT * FROM school.groups WHERE group_id = ?";
		return jdbcTemplate.queryForObject(sql, groupRowMapper, id);
	}

	@Override
	public void update(Group group) throws SQLException {
		String sql = "UPDATE school.groups SET group_name = ? WHERE group_id = ?";
		jdbcTemplate.update(sql, group.getName(), group.getId());
	}

	@Override
	public void delete(int id) throws SQLException {
		String updateStudentsSql = "UPDATE school.students SET group_id = NULL WHERE group_id = ?";
		String deleteGroupSql = "DELETE FROM school.groups WHERE group_id = ?";
		jdbcTemplate.update(updateStudentsSql, id);
		jdbcTemplate.update(deleteGroupSql, id);
	}

	@Override
	public List<Group> getAll() throws SQLException {
		String sql = "SELECT * FROM school.GROUPS";
		return jdbcTemplate.query(sql, groupRowMapper);
	}
}
