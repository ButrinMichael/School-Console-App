package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.util.List;

public class RaceManagerApp {
	public static void main(String[] args) {

        try {
            List<Racer> racersList = RaceManager.generateRacersList();
            RaceManager.processAndDisplayRacers(racersList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}