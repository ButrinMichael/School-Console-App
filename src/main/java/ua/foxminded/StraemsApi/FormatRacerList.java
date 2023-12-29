package ua.foxminded.StraemsApi;

import java.util.List;

public class FormatRacerList {
	public static void formatRacersList(List<Racer> racersList) {
	    int topRacersCount = 15;
	    int position = 1;	    
	    for (Racer racer : racersList) {
	        if (position <= 9) {	            
	            System.out.printf("%d.%-21s | %-30s | %s\n", position++, racer.getName(), racer.getTeam(), MillisecondToTimeConverter.convertMillisecondsToTime(racer.getTimeDifference()));
	        } else {
	        	if (position <= topRacersCount) {	            
		            System.out.printf("%d.%-20s | %-30s | %s\n", position++, racer.getName(), racer.getTeam(), MillisecondToTimeConverter.convertMillisecondsToTime(racer.getTimeDifference()));
	        	}else
	            break;
	        }   
	    }
	    System.out.println("--------------------------------------------------------------------");   
	    for (int i = position; i <= racersList.size(); i++) {
	        Racer racer = racersList.get(i - 1);
	        System.out.printf("%d.%-20s | %-30s | %s\n", i, racer.getName(), racer.getTeam(), MillisecondToTimeConverter.convertMillisecondsToTime(racer.getTimeDifference()));
	    }
	}
}
