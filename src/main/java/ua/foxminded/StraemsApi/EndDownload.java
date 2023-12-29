package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.io.InputStream;

public class EndDownload {

	public void EdnCache(Cache<String, String> cache, String fileName) throws IOException {

		FileReader instance = new FileReader();
		InputStream is = instance.getFileAsIOStream(fileName);
		String fileContent = instance.readFileContent(is);
		String[] lines = fileContent.split("\\r?\\n");

		for (String line : lines) {
			String formattedLog = EndStartFormatLog.formatLog(line);

			String[] parts = formattedLog.split("\t");
			if (parts.length == 2) {
				String identifier = parts[0];
				String timestamp = parts[1];

				cache.put(identifier, timestamp);
			}
		}
	}
}
