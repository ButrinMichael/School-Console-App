package ua.foxminded.StraemsApi;

public class Racer implements Comparable<Racer> {
	private final String name;
	private final String teamName;
	private final Long resultTime;

	public Racer(String name, String teamName, long resultTime) {
		this.name = name;
		this.teamName = teamName;
		this.resultTime = resultTime;
	}

	public String getName() {
		return name;
	}

	public String getTeamName() {
		return teamName;
	}

	public long getResultTime() {
		return resultTime;
	}

	@Override
	public int compareTo(Racer o) {
		return resultTime.compareTo(o.getResultTime());
	}
}
