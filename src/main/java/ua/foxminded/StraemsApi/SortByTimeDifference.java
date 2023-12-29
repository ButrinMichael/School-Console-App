package ua.foxminded.StraemsApi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByTimeDifference {
	public static void sortRacersByTimeDifference(List<Racer> racers) {
		Collections.sort(racers, Comparator.comparingLong(Racer::getTimeDifference));

	}
}
