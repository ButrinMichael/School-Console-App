package ua.foxminded.StraemsApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RaceManager {

    public static List<Racer> generateRacersList() throws IOException {
        CacheImpl cacheEnd = new CacheImpl();
        CacheImpl cacheStart = new CacheImpl();
        CacheImpl cacheNameAbbriviations = new CacheImpl();
        CacheImpl cacheTeamAbbriviations = new CacheImpl();

        EndDownload endDownload = new EndDownload();
        StartDownLoad startDownLoad = new StartDownLoad();
        AbbriviationsNameDownload abbriviationsNameDownload = new AbbriviationsNameDownload();
        AbbriviationsTeamDownload abbriviationsTeamDownload = new AbbriviationsTeamDownload();
        KeyDownLoad keyDownLoad = new KeyDownLoad();

        endDownload.EdnCache(cacheEnd, "end.log");
        startDownLoad.StartCache(cacheStart, "start.log");
        abbriviationsNameDownload.AbbriviationsCache(cacheNameAbbriviations, "abbreviations.txt");
        abbriviationsTeamDownload.AbbriviationsCache(cacheTeamAbbriviations, "abbreviations.txt");

        List<String> firstThreeCharsList = keyDownLoad.getFirstThreeCharsList("start.log");

        List<Racer> racersList = new ArrayList<>();

        for (String key : firstThreeCharsList) {
            String endTime = cacheEnd.get(key);
            String startTime = cacheStart.get(key);

            long timeDifference = TimeDifferenceCalculator.calculateTimeDifference(startTime, endTime);

            Racer racer = new Racer(key, timeDifference, cacheNameAbbriviations.get(key), cacheTeamAbbriviations.get(key));
            racersList.add(racer);
        }

        return racersList;
    }

    public static void processAndDisplayRacers(List<Racer> racersList) {
        SortByTimeDifference.sortRacersByTimeDifference(racersList);
        FormatRacerList.formatRacersList(racersList);
    }
}
