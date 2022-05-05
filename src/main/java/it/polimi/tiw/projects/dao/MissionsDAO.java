package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Mission;
import it.polimi.tiw.projects.beans.MissionStatus;

public class MissionsDAO {
	private Connection connection;

	public MissionsDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Mission> findMissionsByUser(int userId) throws SQLException {
		List<Mission> missions = new ArrayList<Mission>();

		String query = "SELECT * from mission where reporter = ? ORDER BY date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Mission mission = new Mission();
					mission.setId(result.getInt("id"));
					mission.setStartDate(result.getDate("date"));
					mission.setDestination(result.getString("destination"));
					mission.setStatus(result.getInt("status"));
					mission.setDescription(result.getString("description"));
					mission.setDays(result.getInt("days"));
					mission.setReporterID(userId);
					missions.add(mission);
				}
			}
		}
		return missions;
	}

	public Mission findMissionById(int missionId) throws SQLException {
		Mission mission = null;

		String query = "SELECT * FROM mission WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, missionId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					mission = new Mission();
					mission.setId(result.getInt("id"));
					mission.setStartDate(result.getDate("date"));
					mission.setDestination(result.getString("destination"));
					mission.setStatus(result.getInt("status"));
					mission.setDescription(result.getString("description"));
					mission.setDays(result.getInt("days"));
					mission.setReporterID(result.getInt("reporter"));
				}
			}
		}
		return mission;
	}

	public void changeMissionStatus(int missionId, MissionStatus missionStatus) throws SQLException {
		String query = "UPDATE mission SET status = ? WHERE id = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, missionStatus.getValue());
			pstatement.setInt(2, missionId);
			pstatement.executeUpdate();
		}
	}

	public void createMission(Date startDate, int days, String destination, String description, int reporterId)
			throws SQLException {

		String query = "INSERT into mission (date, destination, status, description, days, reporter) VALUES(?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDate(1, new java.sql.Date(startDate.getTime()));
			pstatement.setString(2, destination);
			pstatement.setInt(3, MissionStatus.OPEN.getValue());
			pstatement.setString(4, description);
			pstatement.setInt(5, days);
			pstatement.setInt(6, reporterId);
			pstatement.executeUpdate();
		}
	}

}
