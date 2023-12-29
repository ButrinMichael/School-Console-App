package ua.foxminded.StraemsApi;

public class MillisecondToTimeConverter {
	public static String convertMillisecondsToTime(long milliseconds) {
		long minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000);
		long seconds = (milliseconds % (60 * 1000)) / 1000;
		long millis = milliseconds % 1000;
		String formattedTime = String.format("%02d:%02d.%03d", minutes, seconds, millis);
		return formattedTime;
	}
}
