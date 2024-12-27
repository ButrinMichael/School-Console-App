package ua.SchoolConsoleApp.Services;

import java.util.List;


import ua.SchoolConsoleApp.Group;


public interface GroupService {
	List<Group> findGroupsWithLessOrEqualStudents(int maxStudents);
}
