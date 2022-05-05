package it.polimi.tiw.projects.beans;

public class ExpenseReport {
	private int id;
	private double food;
	private double accomodation;
	private double transportation;
	private int missionId;

	public ExpenseReport() {
		food = 0;
		accomodation = 0;
		transportation = 0;
	}

	public ExpenseReport(int missionId, double food, double accomodation, double transportation) {
		this.food = food;
		this.accomodation = accomodation;
		this.transportation = transportation;
		this.missionId = missionId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getFood() {
		return food;
	}

	public void setFood(double food) {
		this.food = food;
	}

	public double getAccomodation() {
		return accomodation;
	}

	public void setAccomodation(double accomodation) {
		this.accomodation = accomodation;
	}

	public double getTransportation() {
		return transportation;
	}

	public void setTransportation(double transportation) {
		this.transportation = transportation;
	}

	public int getMissioId() {
		return missionId;
	}

	public void setMissionId(int mission_id) {
		this.missionId = mission_id;
	}

}
