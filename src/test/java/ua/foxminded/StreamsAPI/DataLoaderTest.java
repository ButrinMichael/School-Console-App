package ua.foxminded.StreamsAPI;

import ua.foxminded.StraemsApi.DataLoader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

class DataLoaderTest {

	@Test
	void LoadTimeDifferenceMap_shouldReturnValue_whenTimeDifferenceIs() throws IOException {
		DataLoader dataLoader = new DataLoader();
		String startLogContent = "NHR2018-05-24_12:02:49.000\n" + "FAM2018-05-24_12:13:03.000\n"
				+ "KRF2018-05-24_12:02:00.000\n";
		String endLogContent = "NHR2018-05-24_12:02:49.914\n" + "FAM2018-05-24_12:13:04.512\n"
				+ "KRF2018-05-24_12:03:01.250\n";

		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.lines(Paths.get("startFilePath")))
					.thenReturn(Stream.of(startLogContent.split("\n")));
			mockedFiles.when(() -> Files.lines(Paths.get("endFilePath")))
					.thenReturn(Stream.of(endLogContent.split("\n")));
			Map<String, Long> result = dataLoader.loadTimeDifferenceMap("startFilePath", "endFilePath");
			assertThat(result).hasSize(3).containsEntry("NHR", 914L).containsEntry("FAM", 1512L).containsEntry("KRF",
					61250L);
		}
	}

	@Test
	void LoadAbbreviationsMap() throws IOException {
		DataLoader dataLoader = new DataLoader();
		String abbreviationsLogContent = "ABC_name1_team1\n" + "CDU_name2_team2\n";
		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.lines(any())).thenReturn(Stream.of(abbreviationsLogContent.split("\n")));
			Map<String, String[]> result = dataLoader.loadAbbreviationsMap("abbreviationsFilePath");
			assertThat(result).hasSize(2).containsEntry("ABC", new String[] { "name1", "team1" }).containsEntry("CDU",
					new String[] { "name2", "team2" });
		}
	}

}
