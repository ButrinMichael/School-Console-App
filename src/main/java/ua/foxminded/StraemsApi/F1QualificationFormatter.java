package ua.foxminded.StraemsApi;

import java.util.List;

public class F1QualificationFormatter {

    public String formatQualificationReport(List<Racer> racers) {
        StringBuilder result = new StringBuilder();
        int topRacersCount = 15;

        racers.stream().limit(topRacersCount)
                .forEachOrdered(racer -> appendRacerInfo(result, racers.indexOf(racer) + 1, racer));

        appendSeparatorLine(result);

        racers.stream().skip(topRacersCount)
                .forEachOrdered(racer -> appendRacerInfo(result, racers.indexOf(racer) + 1, racer));

        return result.toString();
    }

    private void appendRacerInfo(StringBuilder result, int position, Racer racer) {
        String positionString = (position <= 9) ? position + ". " : position + ".";
        String format = "%-3s %-20s | %-30s | %s\n";
        result.append(String.format(format, positionString, racer.getName(), racer.getTeamName(), formatDuration(racer.getResultTime())));
    }

    private void appendSeparatorLine(StringBuilder result) {
        String format = "%s\n";
        result.append(String.format(format, "_".repeat(69)));  
    }

    private String formatDuration(long milliseconds) {
        long minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (milliseconds % (60 * 1000)) / 1000;
        long millis = milliseconds % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}
