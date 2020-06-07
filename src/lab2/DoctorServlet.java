package lab2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class DoctorServlet extends HttpServlet {

    private void choose(HttpServletRequest request, HttpServletResponse response, DBManager db)
            throws ServletException, IOException {

        request.setAttribute("people", db.getAllDoctors());
        getServletContext().getRequestDispatcher("/jsp/people.jsp").forward(request, response);
    }

    private void selected(HttpServletRequest request, HttpServletResponse response, DBManager db, int id)
            throws ServletException, IOException {

        Doctor currDoctor = db.getDoctorById(id);
        List<Patient> patientsToTreat = db.getPatientsToTreat();
        List<Patient> sickPatients = db.getSickPatients();

        for (Patient patient : patientsToTreat)
            for (Patient patient2 : sickPatients)
                if (patient.id == patient2.id) {
                    sickPatients.remove(patient2);
                    break;
                }

        request.setAttribute("doctor", currDoctor);
        request.setAttribute("patientsToTreat", patientsToTreat);
        request.setAttribute("sickPatients", sickPatients);
        getServletContext().getRequestDispatcher("/jsp/doctor.jsp").forward(request, response);
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

    private String randomTreatment() {
        Random random = new Random();

        return Patient.TREATMENTS[random.nextInt(3)];
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DBManager db;
        try {
            db = DBManager.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e.getCause() + ": " + e.getMessage());
        }

        String id = request.getParameter("id");
        Doctor currDoctor = db.getDoctorById(Integer.parseInt(id));

        if (currDoctor != null) {
            String diagnosePatientId = request.getParameter("diagnose_patient_id");
            if (diagnosePatientId != null) {
                Patient patient = db.getPatientById(Integer.parseInt(diagnosePatientId));

                patient.sickness = request.getParameter("diagnose");
                patient.treatment = randomTreatment();

                if (!db.editPatient(patient.id, null, null, patient.sickness, patient.treatment, null))
                    throw new ServletException("did not edit");
            }

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
        return "Doctor";
    }
}

