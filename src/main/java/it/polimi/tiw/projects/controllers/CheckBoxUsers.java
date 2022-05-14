package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.dao.ConferenceDAO;
import it.polimi.tiw.projects.dao.GuestDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CheckBoxUsers")
public class CheckBoxUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	private Conference conf = new Conference();
	private int attempt = 0;

	public CheckBoxUsers() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

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


		UserBean user = (UserBean) session.getAttribute("user");
		ConferenceDAO conferenceDAO = new ConferenceDAO(connection);
		Conference conference = null;
		try {
			conference = conferenceDAO.findLastConferenceByUser(user.getId());
			if (conference == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			} else if(conference.getId() == this.conf.getId()){
				attempt++;
			} else{
				attempt = 0;
				setConference(conference);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover conference");
			return;
		}


		String[] checkBoxArray = new String[0];
		Boolean isBadRequest = false;
		try {
			checkBoxArray = request.getParameterValues("userscheckbox");

		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}

		if (isBadRequest || checkBoxArray.length < 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		if(conference.getGuests() >= checkBoxArray.length) {
			GuestDAO guestDAO = new GuestDAO(connection);

			try {
				guestDAO.registerGuests(checkBoxArray, conference.getId());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			// Redirect to the Home page and add missions to the parameters
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);

		} else {
			ArrayList<UserBean> users = null;
			UserDAO userDAO = new UserDAO(connection);
			try {
				users = userDAO.getUsers(user.getId());
				if (users == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover users");
				return;
			}

			for(int i=0; i<checkBoxArray.length; i++)
			   for(UserBean ub: users)
				   if(Integer.parseInt(checkBoxArray[i]) == ub.getId())
					   ub.setChecked(true);

			if(attempt >= 2){
				try {
					conferenceDAO.deleteConferenceById(conference.getId());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				// Redirect to the Home page and add missions to the parameters
				String path = getServletContext().getContextPath() + "/Home";
				response.sendRedirect(path);
			} else {
				// Redirect to the Home page and add missions to the parameters
				String path = "/WEB-INF/Anagrafica.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("users", users);
				ctx.setVariable("attempt", 2 - attempt);

				templateEngine.process(path, ctx, response.getWriter());
			}

		}

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setConference(Conference conference) {
		this.conf = conference;
	}
}
