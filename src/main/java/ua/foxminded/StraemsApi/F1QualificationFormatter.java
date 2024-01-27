package ua.foxminded.StraemsApi;

import java.util.List;

public class F1QualificationFormatter {

	private List<Racer> racers;

	public String formatQualificationReport(List<Racer> racers) {
		if (racers == null) {
	        return ""; 
	    }
		
		this.racers = racers;

		StringBuilder result = new StringBuilder();
		int topRacersCount = 15;
		racers.stream().limit(topRacersCount)
				.forEachOrdered(racer -> appendRacerInfo(result, racers.indexOf(racer)));

		appendSeparatorLine(result);

		racers.stream().skip(topRacersCount)
				.forEachOrdered(racer -> appendRacerInfo(result, racers.indexOf(racer) + 1));

		return result.toString();

	}

	private void appendRacerInfo(StringBuilder result, int position) {
		if (position < racers.size()) {
			Racer racer = racers.get(position);
			String positionString = (position + 1 <= 9) ? (position + 1) + ". " : (position + 1) + ".";
			String format = "%-3s %-20s | %-30s | %s\n";
			result.append(String.format(format, positionString, racer.getName(), racer.getTeamName(),
					formatDuration(racer.getResultTime())));
		}
	}

	private void appendSeparatorLine(StringBuilder result) {
		String format = "%s\n";
		result.append(String.format(format, "_".repeat(62))); // Assuming a fixed line length
	}

	private String formatDuration(long milliseconds) {
		long minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000);
		long seconds = (milliseconds % (60 * 1000)) / 1000;
		long millis = milliseconds % 1000;
		return String.format("%02d:%02d.%03d", minutes, seconds, millis);
	}
}