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
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd_HH:mm:ss.SSS";

    public Map<String, String[]> loadAbbreviationsMap(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.map(line -> line.split("_")).distinct()
                    .collect(Collectors.toMap(parts -> parts[0], parts -> new String[]{parts[1], parts[2]}));
        }
    }

    public Map<String, Long> loadTimeDifferenceMap(String startFilePath, String endFilePath) throws IOException {
        try (Stream<String> lines = Stream.concat(Files.lines(Paths.get(startFilePath)),
                Files.lines(Paths.get(endFilePath)))) {
            return lines.collect(
                    Collectors.groupingBy(line -> line.substring(0, 3),
                            Collectors.collectingAndThen(
                                    Collectors.mapping(line -> LocalDateTime.parse(line.substring(3),
                                            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)),
                                            Collectors.toList()),
                                    list -> {
                                        if (list.size() == 2) {
                                            return Duration.between(list.get(0), list.get(1)).toMillis();
                                        } else {
                                            return 0L;
                                        }
                                    })));
        }
    }
}
