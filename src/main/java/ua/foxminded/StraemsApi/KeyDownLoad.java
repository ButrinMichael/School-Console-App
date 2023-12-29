package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KeyDownLoad {

	public List<String> getFirstThreeCharsList(String fileName) throws IOException {
		FileReader instance = new FileReader();
		InputStream is = instance.getFileAsIOStream(fileName);
		String fileContent = instance.readFileContent(is);

		String[] lines = fileContent.split("\\r?\\n");

		List<String> firstThreeCharsList = new ArrayList<>();

		for (String line : lines) {
			String formattedLog = EndStartFormatLog.formatLog(line);

			String[] parts = formattedLog.split("\t");
			if (parts.length == 2) {
				String identifier = parts[0];

				if (identifier.length() >= 3) {
					String firstThreeChars = identifier.substring(0, 3);
					firstThreeCharsList.add(firstThreeChars);
				}
			}
		}
		return firstThreeCharsList;
	}
}
