package it.polimi.tiw.projects.beans;

import java.sql.Date;

public class Mission {
	private int id;
	private Date startDate;
	private String destination;
	private MissionStatus status;
	private String description;
	private int days;
	private int reporterId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStatus(MissionStatus status) {
		this.status = status;
	}

	public void setStatus(int value) {
		this.status = MissionStatus.getMissionStatusFromInt(value);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getReporterId() {
		return reporterId;
	}

	public void setReporterID(int reporter) {
		this.reporterId = reporter;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public MissionStatus getStatus() {
		return status;
	}

	public boolean isOpen() {
		return status == MissionStatus.OPEN;
	}

	public boolean isReported() {
		return status == MissionStatus.REPORTED;
	}

	public boolean isClosed() {
		return status == MissionStatus.CLOSED;
	}

}
