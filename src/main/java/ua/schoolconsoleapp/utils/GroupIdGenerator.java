package ua.schoolconsoleapp.utils;

import java.util.Random;

public class GroupIdGenerator {

	public Integer generateGroupId() {
		Random random = new Random();
		int chance = random.nextInt(100);
		if (chance < 10) {
			return null;
		} else {
			return random.nextInt(10) + 1;
		}
	}
}
