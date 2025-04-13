package ua.schoolconsoleapp.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.repositories.GroupRepository;

@Service
public class GroupServiceImpl implements GroupService {
	
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

	private final GroupRepository groupRepository;

	public GroupServiceImpl(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;

	}
	
	@Override
    public List<Group> findGroupsWithLessOrEqualStudents(int maxStudents) {
        logger.info("Method findGroupsWithLessOrEqualStudents called with maxStudents = {}", maxStudents);
        try {
            List<Group> result = groupRepository.findGroupsWithLessOrEqualStudents(maxStudents);
            logger.info("Found {} groups with student count less than or equal to {}", result.size(), maxStudents);
            return result;
        } catch (RuntimeException e) {
            logger.error("Error occurred while searching for groups with student count <= {}: {}", maxStudents, e.getMessage(), e);
            throw new RuntimeException("Failed to find groups with less or equal students", e);
        }
    }
}
