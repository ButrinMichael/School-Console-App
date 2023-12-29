package ua.foxminded.StraemsApi;

public class Racer {
	private String key;
	private String name;
	private String team;
	private long timeDifference;

	public Racer(String key, long timeDifference, String name, String team) {
		super();
		this.key = key;
		this.timeDifference = timeDifference;
		this.name = name;
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public String getTeam() {
		return team;
	}

	public long getTimeDifference() {
		return timeDifference;
	}

	@Override
	public String toString() {
		return name + " " + team + "  " + timeDifference;
	}

	public void putLapTime(int timeDifference) {
		this.timeDifference = timeDifference;
	}

	public void putName(String name) {
		this.name = name;
	}

	public void putTeam(String team) {
		this.team = team;
	}

	public void printRacerInfo() {
	}
}
