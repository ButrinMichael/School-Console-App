package ua.foxminded.StreamsAPI;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ua.foxminded.StraemsApi.F1QualificationFormatter;
import ua.foxminded.StraemsApi.Racer;

public class F1QualificationFormatterTest {
	@Test
	void formatQualificationReport_shouldReturnFormattedString() {
		F1QualificationFormatter formatter = new F1QualificationFormatter();
		List<Racer> racers = Arrays.asList(new Racer("Driver1", "Team1", 120_000L),
				new Racer("Driver2", "Team2", 120_123L), new Racer("Driver3", "Team3", 999_999L));
		String result = formatter.formatQualificationReport(racers);
		String expectedResult = "1.  Driver1              | Team1                          | 02:00.000\n"
				+ "2.  Driver2              | Team2                          | 02:00.123\n"
				+ "3.  Driver3              | Team3                          | 16:39.999\n"
				+ "______________________________________________________________\n" + "";
		assertEquals(expectedResult, result);

	}

}
