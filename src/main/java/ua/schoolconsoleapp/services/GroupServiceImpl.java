package ua.schoolconsoleapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ua.schoolconsoleapp.dao.JPAGroupDAO;
import ua.schoolconsoleapp.models.Group;

@Service
public class GroupServiceImpl implements GroupService {
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

	private final JPAGroupDAO groupDAO;

	public GroupServiceImpl(JPAGroupDAO groupDAO) {
		this.groupDAO = groupDAO;

	}

	@Override
	public List<Group> findGroupsWithLessOrEqualStudents(int maxStudents) {
		logger.info("Searching for groups with less than or equal to {} students", maxStudents);
		try {
			List<Group> allGroups = groupDAO.getAll();
			return allGroups.stream().filter(group -> group.getStudents().size() <= maxStudents).peek(
					group -> logger.debug("Group {} has {} students", group.getName(), group.getStudents().size()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error while fetching groups with student count <= {}: {}", maxStudents, e.getMessage(), e);
			throw new RuntimeException("Failed to find groups with less or equal students", e);
		}
	}
}
