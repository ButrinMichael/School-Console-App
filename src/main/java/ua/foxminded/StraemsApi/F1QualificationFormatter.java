package ua.foxminded.StraemsApi;

import java.util.List;

public class F1QualificationFormatter {

	public String formatQualificationReport(List<Racer> racers) {
		StringBuilder result = new StringBuilder();
		int topRacersCount = 15;

		int maxNameLength = calculateMaxNameLength(racers);
		int maxTeamNameLength = calculateMaxTeamLength(racers);

		racers.stream().limit(topRacersCount).forEachOrdered(
				racer -> appendRacerInfo(result, racers.indexOf(racer) + 1, racer, maxNameLength, maxTeamNameLength));

		appendSeparatorLine(result, maxNameLength, maxTeamNameLength);

		racers.stream().skip(topRacersCount).forEachOrdered(racer -> appendRacerInfo(result,
				racers.indexOf(racer) + topRacersCount + 1, racer, maxNameLength, maxTeamNameLength));

		return result.toString();
	}

	private void appendRacerInfo(StringBuilder result, int position, Racer racer, int maxNameLength,
			int maxTeamNameLength) {
		String positionString = (position <= 9) ? position + ". " : position + ".";
		result.append(String.format("%-3s %-" + maxNameLength + "s | %-" + maxTeamNameLength + "s | %s\n",
				positionString, racer.getName(), racer.getTeamName(), formatDuration(racer.getResultTime())));
	}

	private int calculateMaxTeamLength(List<Racer> racers) {
		return racers.stream().mapToInt(racer -> racer.getTeamName().length()).max().orElse(0);
	}

	private int calculateMaxNameLength(List<Racer> racers) {
		return racers.stream().mapToInt(racer -> racer.getName().length()).max().orElse(0);
	}

	private void appendSeparatorLine(StringBuilder result, int maxNameLength, int maxTeamNameLength) {
		int lineLength = calculateLineLength(new Racer("", "", 0), maxNameLength, maxTeamNameLength);

		StringBuilder separatorLine = new StringBuilder();
		for (int i = 0; i < lineLength; i++) {
			separatorLine.append("-");
		}

		result.append(separatorLine.toString()).append("\n");
	}

	private int calculateLineLength(Racer racer, int maxNameLength, int maxTeamNameLength) {
		return String.format("%-3d %-" + maxNameLength + "s | %-" + maxTeamNameLength + "s | %s", 1, racer.getName(),
				racer.getTeamName(), formatDuration(racer.getResultTime())).length();
	}

	private String formatDuration(long milliseconds) {
		long minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000);
		long seconds = (milliseconds % (60 * 1000)) / 1000;
		long millis = milliseconds % 1000;
		return String.format("%02d:%02d.%03d", minutes, seconds, millis);
	}
}
