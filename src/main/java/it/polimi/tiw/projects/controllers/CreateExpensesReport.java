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

import it.polimi.tiw.exceptions.BadMissionForExpReport;
import it.polimi.tiw.projects.beans.ExpenseReport;
import it.polimi.tiw.projects.beans.Mission;
import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.ExpenseReportDAO;
import it.polimi.tiw.projects.dao.MissionsDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateExpensesReport")
public class CreateExpensesReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateExpensesReport() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Check params are present and correct
		ExpenseReport expenseReport = null;
		Integer missionId = null;
		try {
			missionId = Integer.parseInt(request.getParameter("missionid"));
			double food = Double.parseDouble(request.getParameter("food"));
			double accomodation = Double.parseDouble(request.getParameter("accomodation"));
			double transportation = Double.parseDouble(request.getParameter("transportation"));
			if (food >= 0 && accomodation >= 0 && transportation >= 0) {
				expenseReport = new ExpenseReport(missionId, food, accomodation, transportation);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		// Excute controller logic
		UserBean user = (UserBean) session.getAttribute("user");
		ExpenseReportDAO expenseReportDAO = new ExpenseReportDAO(connection);
		MissionsDAO missionsDao = new MissionsDAO(connection);
		try {
			Mission mission = missionsDao.findMissionById(missionId);
			if (mission == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Mission not found");
				return;
			}
			if (mission != null && mission.getReporterId() != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed to perform operation");
				return;
			}
			expenseReportDAO.addExpenseReport(expenseReport, mission);
		} catch (SQLException e1) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create report");
			return;
		} catch (BadMissionForExpReport e2) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Not allowed");
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
