package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Mission;
import it.polimi.tiw.projects.beans.MissionStatus;
import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.MissionsDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CloseMission")
public class CloseMission extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CloseMission() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		// get and check params
		Integer missionId = null;
		try {
			missionId = Integer.parseInt(request.getParameter("missionid"));
		} catch (NumberFormatException | NullPointerException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// Update mission status
		UserBean user = (UserBean) session.getAttribute("user");
		MissionsDAO missionsDao = new MissionsDAO(connection);

		try {
			// Check that only the user who created the mission edits it
			Mission mission = missionsDao.findMissionById(missionId);
			if (mission == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Mission not found");
				return;
			}
			if (mission.getReporterId() != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not allowed");
				return;
			}
			missionsDao.changeMissionStatus(missionId, MissionStatus.CLOSED);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mission not closable");
			return;
		}

		// Return view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetMissionDetails?missionid=" + missionId;
		response.sendRedirect(path);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
