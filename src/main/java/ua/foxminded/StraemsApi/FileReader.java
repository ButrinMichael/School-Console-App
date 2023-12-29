package ua.foxminded.StraemsApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

	public class FileReader {
	    public InputStream getFileAsIOStream(final String fileName) {
	        InputStream ioStream = this.getClass()
	                .getClassLoader()
	                .getResourceAsStream(fileName);

	        if (ioStream == null) {
	            throw new IllegalArgumentException(fileName + " is not found");
	        }
	        return ioStream;
	    }

	    public String readFileContent(InputStream is) throws IOException {
	        try (InputStreamReader isr = new InputStreamReader(is);
	             BufferedReader br = new BufferedReader(isr)) {
	            return br.lines().collect(Collectors.joining(System.lineSeparator()));
	        }
	        }
	    }
	

