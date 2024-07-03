package ua.SchoolConsoleApp.DB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

public class DB_file_reader {

	public static void executeSQLFile(String fileName) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DatabaseConnection.getConnection();
			InputStream inputStream = DB_file_reader.class.getResourceAsStream("/" + fileName);

			Statement statement = connection.createStatement();
			if (inputStream != null) {
				byte[] buffer = new byte[inputStream.available()];
				inputStream.read(buffer);
				String sql = new String(buffer);
				statement.executeUpdate(sql);
			} else {
				System.err.println("File not found: " + fileName);
			}
			statement.close();
			connection.close();
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
			System.err.println("Error executing SQL script: " + e.getMessage());
			e.printStackTrace();
		}
	}
}