package ua.schoolconsoleapp.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(DBFileReader.class);
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public DBFileReader(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void executeSQLFile(String fileName) {
		logger.info("Starting execution of SQL file: {}", fileName);
		try {
			ClassPathResource resource = new ClassPathResource(fileName);
			Path path = Paths.get(resource.getURI());
			String sql = Files.readString(path);

			jdbcTemplate.execute(sql);
			logger.info("Successfully executed SQL file: {}", fileName);
		} catch (IOException e) {
			logger.error("File not found or unable to read file: {}", fileName, e);
			System.err.println("File not found or unable to read: " + fileName);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error executing SQL script from file: {}", fileName, e);
			System.err.println("Error executing SQL script: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
