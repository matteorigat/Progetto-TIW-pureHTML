package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Conference;
import it.polimi.tiw.projects.dao.ConferenceDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.ExpenseReport;
import it.polimi.tiw.projects.beans.UserBean;
import it.polimi.tiw.projects.dao.ExpenseReportDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetConferenceDetails")
public class GetConferenceDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public GetConferenceDetails() {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If the user is not logged in (not present in session) redirect to the login
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }

        // get and check params
        Integer conferenceId = null;
        try {
            conferenceId = Integer.parseInt(request.getParameter("conferenceid"));
        } catch (NumberFormatException | NullPointerException e) {
            // only for debugging e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        // If a mission with that ID exists for that USER,
        // obtain the expense report for it
        UserBean user = (UserBean) session.getAttribute("user");
        ConferenceDAO conferenceDAO = new ConferenceDAO(connection);
        ExpenseReport expenses = new ExpenseReport();
        Conference conference = null;
        try {
            conference = conferenceDAO.findConferenceById(conferenceId);
            if (conference == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                return;
            }
            if (conference.getHostId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                return;
            }
            ExpenseReportDAO expenseReportDAO = new ExpenseReportDAO(connection);
            expenses = expenseReportDAO.findExpensesForMission(conferenceId);
        } catch (SQLException e) {
            e.printStackTrace();
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover mission");
            return;
        }

        // Redirect to the Home page and add missions to the parameters
        String path = "/WEB-INF/MissionDetails.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("mission", conference);
        ctx.setVariable("expenses", expenses);
        templateEngine.process(path, ctx, response.getWriter());
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}