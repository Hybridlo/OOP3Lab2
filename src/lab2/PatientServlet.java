package lab2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class PatientServlet extends HttpServlet {

    private void choose(HttpServletRequest request, HttpServletResponse response, DBManager db)
            throws ServletException, IOException {

        request.setAttribute("people", db.getAllPatients());
        getServletContext().getRequestDispatcher("/jsp/people.jsp").forward(request, response);
    }

    private void select(HttpServletRequest request, HttpServletResponse response, DBManager db, int id)
            throws ServletException, IOException {

        Patient currPatient = db.getPatientById(id);

        request.setAttribute("patient", currPatient);
        getServletContext().getRequestDispatcher("/jsp/patient.jsp").forward(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DBManager db;
        try {
            db = DBManager.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e.getCause() + ": " + e.getMessage());
        }

        String id = request.getParameter("id");

        if (id != null && Integer.parseInt(id) > 0)
            select(request, response, db, Integer.parseInt(id));

        choose(request, response, db);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DBManager db;
        try {
            db = DBManager.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e.getCause() + ": " + e.getMessage());
        }

        String id = request.getParameter("id");
        Patient currPatient = db.getPatientById(Integer.parseInt(id));
        if (currPatient != null) {
            currPatient.isSick = true;
            db.editPatient(currPatient.id, null, true, null, null);
        }

        request.setAttribute("patient", currPatient);
        getServletContext().getRequestDispatcher("/jsp/patient.jsp").forward(request, response);
    }

    public String getServletInfo() {
        return "Patient";
    }
}
