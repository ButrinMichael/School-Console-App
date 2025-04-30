package ua.schoolconsoleapp.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
=======
import ua.schoolconsoleapp.dao.JPAGroupDAO;
>>>>>>> refs/remotes/origin/main
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.repositories.GroupRepository;

@Service
public class GroupServiceImpl implements GroupService {
	
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

<<<<<<< HEAD
	private final GroupRepository groupRepository;
=======
	private final JPAGroupDAO groupDAO;
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
	public GroupServiceImpl(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;
=======
	public GroupServiceImpl(JPAGroupDAO groupDAO) {
		this.groupDAO = groupDAO;
>>>>>>> refs/remotes/origin/main

	}
	
	@Override
    public List<Group> findGroupsWithLessOrEqualStudents(int maxStudents) {
        logger.info("Method findGroupsWithLessOrEqualStudents called with maxStudents = {}", maxStudents);
        try {
<<<<<<< HEAD
            List<Group> result = groupRepository.findGroupsWithLessOrEqualStudents(maxStudents);
=======
            List<Group> result = groupDAO.findGroupsWithLessOrEqualStudents(maxStudents);
>>>>>>> refs/remotes/origin/main
            logger.info("Found {} groups with student count less than or equal to {}", result.size(), maxStudents);
            return result;
        } catch (RuntimeException e) {
            logger.error("Error occurred while searching for groups with student count <= {}: {}", maxStudents, e.getMessage(), e);
            throw new RuntimeException("Failed to find groups with less or equal students", e);
        }
    }
}
