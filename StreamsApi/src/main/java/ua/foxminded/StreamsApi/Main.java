package ua.foxminded.StreamsApi;
import java.time.Duration;
import java.time.LocalTime;
public class Main {
	


	
		public static void main(String[] args) {

			
			String time1 = "12:19:25.111";
			String time2 = "12:20:56.211";

			long seconds = Duration.between(LocalTime.parse(time1), LocalTime.parse(time2)).toMillis();
			System.out.println(seconds);
			System.out.println(seconds/1000/60 + ":" +     
			 ( seconds/1000-((seconds/1000/60)*60)) + "." +
					(seconds)    );
			
		}
	}


