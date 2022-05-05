package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.exceptions.BadMissionForExpReport;
import it.polimi.tiw.projects.beans.ExpenseReport;
import it.polimi.tiw.projects.beans.Mission;
import it.polimi.tiw.projects.beans.MissionStatus;

public class ExpenseReportDAO {
	private Connection connection;

	public ExpenseReportDAO(Connection connection) {
		this.connection = connection;
	}

	public ExpenseReport findExpensesForMission(int missionId) throws SQLException {
		ExpenseReport expenseReport = new ExpenseReport();

		String query = "SELECT * FROM expenses WHERE mission = ? ";
		// example of try with resources, just for fun
		// https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, missionId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					expenseReport.setFood(result.getDouble("food"));
					expenseReport.setAccomodation(result.getDouble("accomodation"));
					expenseReport.setTransportation(result.getDouble("transport"));
					expenseReport.setMissionId(result.getInt("mission"));
				}
			}
		}
		return expenseReport;
	}

	public void addExpenseReport(ExpenseReport expenseReport, Mission mission)
			throws SQLException, BadMissionForExpReport {
		// Check that the mission exists and is in OPEN state
		if (mission == null | mission.getStatus() != MissionStatus.OPEN) {
			throw new BadMissionForExpReport("Mission cannot introduce expense report");
		}
		MissionsDAO missionDAO = new MissionsDAO(connection);

		String query = "INSERT into expenses (food, accomodation, transport, mission) VALUES(?, ?, ?, ?)";
		// Delimit the transaction explicitly, not to leave the db in inconsistent state
		connection.setAutoCommit(false);
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDouble(1, expenseReport.getFood());
			pstatement.setDouble(2, expenseReport.getAccomodation());
			pstatement.setDouble(3, expenseReport.getTransportation());
			pstatement.setInt(4, expenseReport.getMissioId());
			pstatement.executeUpdate(); // 1st update
			// 2nd update
			missionDAO.changeMissionStatus(expenseReport.getMissioId(), MissionStatus.REPORTED);
			connection.commit();
		} catch (SQLException e) {
			connection.rollback(); // if update 1 OR 2 fails, roll back all work
			throw e;
		}
	}
}
