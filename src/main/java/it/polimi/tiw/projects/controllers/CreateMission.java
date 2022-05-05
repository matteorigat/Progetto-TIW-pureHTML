package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.MissionsDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateMission")
public class CreateMission extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateMission() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	private Date getMeYesterday() {
		return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
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

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		Date startDate = null;
		String destination = null;
		String description = null;
		Integer days = null;
		try {
			days = Integer.parseInt(request.getParameter("days"));
			destination = StringEscapeUtils.escapeJava(request.getParameter("destination"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = (Date) sdf.parse(request.getParameter("date"));
			isBadRequest = days <= 0 || destination.isEmpty() || description.isEmpty()
					|| getMeYesterday().after(startDate);
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// Create mission in DB
		UserBean user = (UserBean) session.getAttribute("user");
		MissionsDAO missionsDAO = new MissionsDAO(connection);
		try {
			missionsDAO.createMission(startDate, days, destination, description, user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create mission");
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Home";
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
