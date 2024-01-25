package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class DataLoader {
    private static final String dataTimePattern = "yyyy-MM-dd_HH:mm:ss.SSS";

    public Map<String, String[]> loadAbbreviationsMap(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.map(line -> line.split("_")).distinct()
                    .collect(Collectors.toMap(parts -> parts[0], parts -> new String[]{parts[1], parts[2]}));
        }
    }

    public Map<String, Long> loadTimeDifferenceMap(String startFilePath, String endFilePath) throws IOException {
        try (Stream<String> startLines = Files.lines(Paths.get(startFilePath));
             Stream<String> endLines = Files.lines(Paths.get(endFilePath))) {

            Map<String, LocalDateTime> startTimes = startLines.collect(Collectors.toMap(
                    line -> line.substring(0, 3),
                    line -> LocalDateTime.parse(line.substring(3), DateTimeFormatter.ofPattern(dataTimePattern))
            ));

            Map<String, LocalDateTime> endTimes = endLines.collect(Collectors.toMap(
                    line -> line.substring(0, 3),
                    line -> LocalDateTime.parse(line.substring(3), DateTimeFormatter.ofPattern(dataTimePattern))
            ));

            return startTimes.entrySet().stream()
                    .filter(entry -> endTimes.containsKey(entry.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> Duration.between(entry.getValue(), endTimes.get(entry.getKey())).toMillis()
                    ));
        }
    }
                                    
}
