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

	@Override
	public Group read(int id) throws SQLException {
		return jdbcTemplate.queryForObject(SelectGroupByIdSQL, groupRowMapper, id);
	}

	@Override
	public void update(Group group) throws SQLException {
		jdbcTemplate.update(UpdateGroupsSQL, group.getName(), group.getId());
	}

	@Override
	public void delete(int id) throws SQLException {
		jdbcTemplate.update(UpdateStudentsGroupByGroupIdSQL, id);
		jdbcTemplate.update(DeleteGroupByIdSQL, id);
	}

	@Override
	public List<Group> getAll() throws SQLException {
		return jdbcTemplate.query(GetAllGroupsSQL, groupRowMapper);
	}
}
