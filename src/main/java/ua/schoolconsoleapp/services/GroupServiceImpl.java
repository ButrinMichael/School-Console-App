package ua.schoolconsoleapp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ua.schoolconsoleapp.dao.GroupDAO;
import ua.schoolconsoleapp.dao.StudentsDAO;
import ua.schoolconsoleapp.models.Group;

@Service
public class GroupServiceImpl implements GroupService{
    private final GroupDAO groupDAO;
    private final StudentsDAO studentsDAO;

    public GroupServiceImpl(GroupDAO groupDAO, StudentsDAO studentsDAO) {
        this.groupDAO = groupDAO;
        this.studentsDAO = studentsDAO;
    }

    @Override
    public List<Group> findGroupsWithLessOrEqualStudents(int maxStudents) {
        List<Group> result = new ArrayList<>();
        try {
            List<Group> allGroups = groupDAO.getAll();
            for (Group group : allGroups) {
                int numStudents = studentsDAO.getNumStudentsInGroup(group.getId());
                if (numStudents <= maxStudents) {
                    result.add(group);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find groups with less or equal students", e);
        }
        return result;
    }
}
