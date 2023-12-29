package ua.foxminded.StraemsApi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeDifferenceCalculator {
	public static long calculateTimeDifference(String dateTimeString1, String dateTimeString2) {
		// Удаление пробелов в начале и конце строки
		dateTimeString1 = dateTimeString1.trim();
		dateTimeString2 = dateTimeString2.trim();

		// Парсинг строк в LocalDateTime
		LocalDateTime dateTime1 = LocalDateTime.parse(dateTimeString1,
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS"));
		LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeString2,
				DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS"));

		// Вычисление разницы во времени
		Duration duration = Duration.between(dateTime1, dateTime2);

		// Получение разницы в миллисекундах
		long timeDifferenceMillis = duration.toMillis();

		return timeDifferenceMillis;
	}
}
