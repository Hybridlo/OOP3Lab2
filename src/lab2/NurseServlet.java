package lab2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class NurseServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DBManager db;
        try {
            db = DBManager.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e.getCause() + ": " + e.getMessage());
        }

        request.setAttribute("people", db.getAllNurses());
        getServletContext().getRequestDispatcher("/jsp/people.jsp").forward(request, response);
    }

    public String getServletInfo() {
        return "ChooseNurse";
    }
}
