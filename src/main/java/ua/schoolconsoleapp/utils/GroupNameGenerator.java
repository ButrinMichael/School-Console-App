package ua.schoolconsoleapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupNameGenerator {

	public static List<String> generateGroupNames(int count) {
		List<String> names = new ArrayList<>();
		Random random = new Random();

		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String digits = "0123456789";

		for (int i = 0; i < count; i++) {
			StringBuilder nameBuilder = new StringBuilder();
			for (int j = 0; j < 2; j++) {
				nameBuilder.append(letters.charAt(random.nextInt(letters.length())));
			}
			nameBuilder.append('-');
			for (int j = 0; j < 2; j++) {
				nameBuilder.append(digits.charAt(random.nextInt(digits.length())));
			}
			names.add(nameBuilder.toString());
		}

		return names;
	}
}