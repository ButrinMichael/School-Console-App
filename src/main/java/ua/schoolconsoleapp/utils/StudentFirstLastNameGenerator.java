package ua.schoolconsoleapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentFirstLastNameGenerator {
	private static final String[] firstNames = { "Max", "Paul", "Leon", "Luca", "Finn", "Liam", "Elias", "Jonas",
			"Noah", "Felix", "Sophie", "Marie", "Emma", "Hannah", "Lena", "Mia", "Emilia", "Anna", "Laura", "Lea" };

	private static final String[] lastNames = { "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner",
			"Becker", "Schulz", "Hoffmann", "Klein", "Wolf", "Schröder", "Neumann", "Schwarz", "Zimmermann", "Braun",
			"Hofmann", "Krüger", "König" };

	public static List<String> generateStudents(int count) {
		List<String> students = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < count; i++) {
			String firstName = firstNames[random.nextInt(firstNames.length)];
			String lastName = lastNames[random.nextInt(lastNames.length)];
			students.add(firstName + " " + lastName);
		}

		return students;
	}
}
