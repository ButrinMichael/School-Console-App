package ua.schoolconsoleapp.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DBFileReader {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public DBFileReader(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void executeSQLFile(String fileName) {
		try {
			ClassPathResource resource = new ClassPathResource(fileName);
			Path path = Paths.get(resource.getURI());
			String sql = Files.readString(path);

			jdbcTemplate.execute(sql);
		} catch (IOException e) {
			System.err.println("File not found or unable to read: " + fileName);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error executing SQL script: " + e.getMessage());
			e.printStackTrace();
		}
	}
}