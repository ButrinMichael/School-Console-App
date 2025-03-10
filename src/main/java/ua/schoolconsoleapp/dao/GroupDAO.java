package ua.schoolconsoleapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ua.schoolconsoleapp.models.Group;

@Repository
public class GroupDAO implements Dao<Group> {

	private static final Logger logger = LoggerFactory.getLogger(GroupDAO.class);
	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_GROUP_SQL = "INSERT INTO school.GROUPS (group_name) VALUES (?)";
	private static final String SELECT_GROUP_BY_ID_SQL = "SELECT * FROM school.GROUPS WHERE group_id = ?";
	private static final String UPDATE_GROUPS_SQL = "UPDATE school.GROUPS SET group_name = ? WHERE group_id = ?";
	private static final String UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL = "UPDATE school.students SET group_id = NULL WHERE group_id = ?";
	private static final String DELETE_GROUP_BY_ID_SQL = "DELETE FROM school.GROUPS WHERE group_id = ?";
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
        logger.info("Creating group with name: {}", group.getName());
        try {
            jdbcTemplate.update(INSERT_GROUP_SQL, group.getName());
            logger.info("Group '{}' created successfully.", group.getName());
        } catch (DataAccessException e) {
            logger.error("Error creating group '{}': {}", group.getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to create group", e);
        }
    }

    public Optional<Group> read(int id) {
        logger.info("Reading group with ID: {}", id);
        try {
            Optional<Group> group = jdbcTemplate.query(SELECT_GROUP_BY_ID_SQL, groupRowMapper, id).stream().findFirst();
            if (group.isPresent()) {
                logger.info("Group with ID {} found: {}", id, group.get());
            } else {
                logger.warn("Group with ID {} not found.", id);
            }
            return group;
        } catch (DataAccessException e) {
            logger.error("Error reading group with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to read group", e);
        }
    }

    @Override
    public void update(Group group) {
        logger.info("Updating group with ID: {} and new name: {}", group.getId(), group.getName());
        try {
            jdbcTemplate.update(UPDATE_GROUPS_SQL, group.getName(), group.getId());
            logger.info("Group with ID {} updated successfully.", group.getId());
        } catch (DataAccessException e) {
            logger.error("Error updating group with ID {}: {}", group.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update group", e);
        }
    }

	@Override
	@Transactional
	public void delete(int id) {
        logger.info("Deleting group with ID: {}", id);
        try {
            jdbcTemplate.update(UPDATE_STUDENTS_GROUP_BY_GROUP_ID_SQL, id);
            jdbcTemplate.update(DELETE_GROUP_BY_ID_SQL, id);
            logger.info("Group with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting group with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete group", e);
        }
    }

	public List<Group> getAll() {
		logger.info("Fetching all groups");
		try {
			List<Group> groups = jdbcTemplate.query(GET_ALL_GROUP_SQL, groupRowMapper);
			 logger.info("Fetched {} groups from database.", groups.size());
			 return groups;
		} catch (DataAccessException e) {
			System.err.println("Error fetching all groups: " + e.getMessage());
			logger.error("Error fetching all groups: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to fetch groups", e);
		}
	}

}
