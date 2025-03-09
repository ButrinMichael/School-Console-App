package ua.schoolconsoleapp.services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.schoolconsoleapp.dao.GroupDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.models.Group;

@Service
public class GroupServiceImpl implements GroupService{
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
	
    private final GroupDAO groupDAO;
    private final StudentsDAO studentsDAO;

    public GroupServiceImpl(GroupDAO groupDAO, StudentsDAO studentsDAO) {
        this.groupDAO = groupDAO;
        this.studentsDAO = studentsDAO;
    }

    @Override
    public List<Group> findGroupsWithLessOrEqualStudents(int maxStudents) {
    	logger.info("Method findGroupsWithLessOrEqualStudents called with maxStudents = {}", maxStudents);
        List<Group> result = new ArrayList<>();
        try {
            List<Group> allGroups = groupDAO.getAll();
            logger.debug("Retrieved {} groups from the database", allGroups.size());
            for (Group group : allGroups) {
                int numStudents = studentsDAO.getNumStudentsInGroup(group.getId());
                logger.debug("Group with ID={} contains {} students", group.getId(), numStudents);
                if (numStudents <= maxStudents) {
                	result.add(group);
                	logger.debug("Group with ID={} added to the result because it contains {} students (less than or equal to {})",group.getId(), numStudents, maxStudents);
                    }
            }
            logger.info("Found {} groups with student count less than or equal to {}", result.size(), maxStudents);
        } catch (RuntimeException e) {
        	logger.error("Error occurred while searching for groups with student count <= {}: {}", maxStudents, e.getMessage(), e);
            throw new RuntimeException("Failed to find groups with less or equal students", e);
        }
        return result;
    }
}
