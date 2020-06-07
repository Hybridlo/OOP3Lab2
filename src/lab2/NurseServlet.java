package lab2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NurseServlet extends HttpServlet {

    private void choose(HttpServletRequest request, HttpServletResponse response, DBManager db)
            throws ServletException, IOException {

        request.setAttribute("people", db.getAllNurses());
        getServletContext().getRequestDispatcher("/jsp/people.jsp").forward(request, response);
    }

    private void selected(HttpServletRequest request, HttpServletResponse response, DBManager db, int id)
            throws ServletException, IOException {

        Nurse currNurse = db.getNurseById(id);
        List<Patient> patients = db.getPatientsToTreat();
        List<Patient> patientsToTreat = new ArrayList<>();  //use two lists, concurrentModificationException otherwise

        for (Patient patient : patients)
            if (!patient.treatment.equals(Patient.TREATMENTS[Patient.ONLY_DOC_TREATMENT]))
                patientsToTreat.add(patient);

        request.setAttribute("nurse", currNurse);
        request.setAttribute("patientsToTreat", patientsToTreat);
        getServletContext().getRequestDispatcher("/jsp/nurse.jsp").forward(request, response);
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
            selected(request, response, db, Integer.parseInt(id));

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
        Nurse currNurse = db.getNurseById(Integer.parseInt(id));

        if (currNurse != null) {
            String treatPatientId = request.getParameter("treat_patient_id");
            if (treatPatientId != null) {
                Patient patient = db.getPatientById(Integer.parseInt(treatPatientId));

                patient.isSick = false;
                patient.treatment = "";
                if (patient.sicknessHistory == null || patient.sicknessHistory.equals(""))
                    patient.sicknessHistory = patient.sickness;
                else
                    patient.sicknessHistory += ", " + patient.sickness;

                patient.sickness = "";

                db.editPatient(patient.id, null, false, patient.sickness, patient.treatment, patient.sicknessHistory);
            }
        }

        selected(request, response, db, Integer.parseInt(id));
    }

    public String getServletInfo() {
        return "Nurse";
    }
}
