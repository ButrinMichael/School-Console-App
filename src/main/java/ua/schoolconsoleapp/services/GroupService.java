package ua.schoolconsoleapp.services;

import java.util.List;

import ua.schoolconsoleapp.entity.Group;


public interface GroupService {
	List<Group> findGroupsWithLessOrEqualStudents(int maxStudents);
}
