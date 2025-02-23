package ua.schoolconsoleapp.utils;

import java.util.ArrayList;
import java.util.List;

public class CourseList {

	public static List<String> CourseNames() {
		List<String> courseNames = new ArrayList<>();
		courseNames.add("Mathematics");
		courseNames.add("Biology");
		courseNames.add("Physics");
		courseNames.add("Computer Science");
		courseNames.add("Chemistry");
		courseNames.add("History");
		courseNames.add("Literature");
		courseNames.add("Geography");
		courseNames.add("Foreign Language");
		courseNames.add("Art");

		return courseNames;
	}
}
