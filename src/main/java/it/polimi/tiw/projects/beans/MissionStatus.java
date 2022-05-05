package it.polimi.tiw.projects.beans;

//Enum: https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
public enum MissionStatus {
	OPEN(0), REPORTED(1), CLOSED(2);

	private final int value;

	MissionStatus(int value) {
		this.value = value;
	}

	public static MissionStatus getMissionStatusFromInt(int value) {
		switch (value) {
		case 0:
			return MissionStatus.OPEN;
		case 1:
			return MissionStatus.REPORTED;
		case 2:
			return MissionStatus.CLOSED;
		}
		return null;
	}

	public int getValue() {
		return value;
	}

}
