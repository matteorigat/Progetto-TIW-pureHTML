package it.polimi.tiw.projects.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.beans.Mission;
import it.polimi.tiw.projects.beans.MissionStatus;

public class ConferenceDAO {
    private Connection connection;

    public ConferenceDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Conference> findConferenceByUser(int userId) throws SQLException {
        List<Conference> conferences = new ArrayList<Conference>();

        String query = "SELECT * from conferences where host = ? ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Conference conference = new Conference();
                    conference.setId(result.getInt("id"));
                    conference.setTitle(result.getString("title"));
                    conference.setDate(result.getTimestamp("date"));
                    conference.setDuration(result.getTime("duration"));
                    conference.setGuests(result.getInt("guests"));
                    conference.setHostId(userId);
                    conferences.add(conference);
                }
            }
        }
        return conferences;
    }

    public List<Conference> findConference2ByUser(int userId) throws SQLException {
        List<Conference> conferences = new ArrayList<Conference>();

        String query = "SELECT * from conferences where id in (SELECT idconference from guests where idguest = ?) ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Conference conference = new Conference();
                    conference.setId(result.getInt("id"));
                    conference.setTitle(result.getString("title"));
                    conference.setDate(result.getTimestamp("date"));
                    conference.setDuration(result.getTime("duration"));
                    conference.setGuests(result.getInt("guests"));
                    conference.setHostId(userId);
                    conferences.add(conference);
                }
            }
        }
        return conferences;
    }

    public Conference findConferenceById(int conferenceId) throws SQLException {
        Conference conference = null;
        String query = "SELECT * FROM conferences WHERE id = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, conferenceId);
            try (ResultSet result = pstatement.executeQuery();) {
                if (result.next()) {
                    conference = new Conference();
                    conference.setId(result.getInt("id"));
                    conference.setHostId(result.getInt("host"));
                    conference.setTitle(result.getString("title"));
                    conference.setDate(result.getTimestamp("date"));
                    conference.setDuration(result.getTime("duration"));
                    conference.setGuests(result.getInt("guests"));
                }
            }
        }
        return conference;
    }

    public void changeMissionStatus(int missionId, MissionStatus missionStatus) throws SQLException {
        String query = "UPDATE mission SET status = ? WHERE id = ? ";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, missionStatus.getValue());
            pstatement.setInt(2, missionId);
            pstatement.executeUpdate();
        }
    }

    public void createConference(String title, Timestamp date, Time duration, int guests, int host)
            throws SQLException {

        String query = "INSERT into conferences (host, title, date, duration, guests) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setInt(1, host);
            pstatement.setString(2, title);
            pstatement.setTimestamp(3, date);
            pstatement.setTime(4, duration);
            pstatement.setInt(5, guests);
            pstatement.executeUpdate();
        }
    }

}