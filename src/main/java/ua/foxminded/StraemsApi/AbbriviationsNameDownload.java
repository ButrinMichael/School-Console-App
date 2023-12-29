package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.io.InputStream;

public class AbbriviationsNameDownload {
	public void AbbriviationsCache(Cache<String, String> cache, String fileName) throws IOException {

		FileReader instance = new FileReader();
		InputStream is = instance.getFileAsIOStream(fileName);
		String fileContent = instance.readFileContent(is);

		String[] lines = fileContent.split("\\r?\\n");
		for (String line : lines) {
			String[] parts = line.split("_");

			if (parts.length == 3) {
				String key = parts[0];
				String name = parts[1];
				cache.put(key, name);
			}
		}
	}
}
