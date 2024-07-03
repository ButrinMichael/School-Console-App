package ua.SchoolConsoleApp.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ua.SchoolConsoleApp.Group;

public class GroupDAO implements Dao<Group> {
	private final Connection connection;

	public GroupDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(Group group) throws SQLException {
		String sql = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, group.getName());
			statement.executeUpdate();
		}
	}

	@Override
	public Group read(int id) throws SQLException {
		Group group = null;
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT group_name FROM school.groups WHERE group_id = ?");
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String groupName = resultSet.getString("group_name");
				group = new Group(id, groupName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	@Override
	public void update(Group entity) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("UPDATE school.groups SET group_name = ? WHERE group_id = ?");
			statement.setString(1, entity.getName());
			statement.setInt(2, entity.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		try {
			PreparedStatement updateStatement = connection
					.prepareStatement("UPDATE school.students SET group_id = NULL WHERE group_id = ?");
			updateStatement.setInt(1, id);
			updateStatement.executeUpdate();

			PreparedStatement deleteStatement = connection
					.prepareStatement("DELETE FROM school.groups WHERE group_id = ?");
			deleteStatement.setInt(1, id);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Group> getAll() {
		List<Group> groups = new ArrayList<>();
		String sql = "SELECT * FROM school.GROUPS";
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				int id = resultSet.getInt("group_id");
				String name = resultSet.getString("group_name");
				groups.add(new Group(id, name));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groups;
	}

}
