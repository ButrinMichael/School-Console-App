package ua.foxminded.StraemsApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndStartFormatLog {
	
	static String formatLog(String log) {
		Pattern pattern = Pattern.compile("([A-Z]+)(\\d{4}-\\d{2}-\\d{2}_\\d{2}:\\d{2}:\\d{2}\\.\\d+)");
		Matcher matcher = pattern.matcher(log);

		if (matcher.find()) {
			String identifier = matcher.group(1);
			String timestamp = matcher.group(2);

			return identifier + "\t" + timestamp + System.lineSeparator();
		} else {
			return "";
		}
	}
}
