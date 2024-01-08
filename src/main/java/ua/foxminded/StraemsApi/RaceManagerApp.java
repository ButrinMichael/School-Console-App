package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RaceManagerApp {

	public static void main(String[] args) throws IOException {

		Map<String, String[]> abbreviationsMap = DataLoader
				.loadAbbreviationsMap("src/main/resources/abbreviations.txt");
		Map<String, Long> finishingTimeMap = DataLoader.loadTimeDifferenceMap("src/main/resources/start.log",
				"src/main/resources/end.log");

		List<Racer> racers = finishingTimeMap.entrySet().stream().map(entry -> {
			String abbreviation = entry.getKey();
			String[] racerInfo = abbreviationsMap.getOrDefault(abbreviation, new String[] { "Unknown", "Unknown" });
			String name = racerInfo[0];
			String teamName = racerInfo[1];
			long resultTime = entry.getValue();

			return new Racer(name, teamName, resultTime);
		}).sorted().collect(Collectors.toList());

		F1QualificationFormatter.printQualificationReport(racers);

	}
}
