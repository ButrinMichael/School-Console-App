package ua.foxminded.StraemsApi;

import java.util.List;

public class F1QualificationFormatter {
	public static void printQualificationReport(List<Racer> racers) {

		int topRacersCount = 15;

		for (int i = 0; i < topRacersCount; i++) {
			Racer racer = racers.get(i);
			printRacerInfo(i + 1, racer, calculatemaxNameLength(racers), calculatemaxTeamLength(racers));
		}
		printSeparatorLine(racers);

		for (int i = topRacersCount; i < racers.size(); i++) {
			Racer racer = racers.get(i);
			printRacerInfo(i + 1, racer, calculatemaxNameLength(racers), calculatemaxTeamLength(racers));
		}
	}

	private static void printRacerInfo(int position, Racer racer, int maxNameLength, int maxTeamNameLength) {
		String positionString = (position <= 9) ? position + ". " : position + ".";
		System.out.printf("%-3s %-" + maxNameLength + "s | %-" + maxTeamNameLength + "s | %s\n", positionString,
				racer.getName(), racer.getTeamName(), FormatDuration.formatDuration(racer.getResultTime()));
	}

	public static int calculatemaxTeamLength(List<Racer> racers) {
		return racers.stream().mapToInt(racer -> racer.getTeamName().length()).max().orElse(0);
	}

	public static int calculatemaxNameLength(List<Racer> racers) {
		return racers.stream().mapToInt(racer -> racer.getName().length()).max().orElse(0);
	}

	private static void printSeparatorLine(List<Racer> racers) {
		int maxNameLength = calculatemaxNameLength(racers);
		int maxTeamNameLength = calculatemaxTeamLength(racers);
		int lineLength = calculateLineLength(new Racer("", "", 0), maxNameLength, maxTeamNameLength);

		StringBuilder separatorLine = new StringBuilder();
		for (int i = 0; i < lineLength; i++) {
			separatorLine.append("-");
		}

		System.out.println(separatorLine.toString());
	}

	private static int calculateLineLength(Racer racer, int maxNameLength, int maxTeamNameLength) {
		return String.format("%-3d %-" + maxNameLength + "s | %-" + maxTeamNameLength + "s | %s", 1, racer.getName(),
				racer.getTeamName(), FormatDuration.formatDuration(racer.getResultTime())).length();
	}
}
