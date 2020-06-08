package lab2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoctorServletTest {
    DBManager db;
    HttpServletRequest request;
    HttpServletResponse response;
    ServletContext context;
    RequestDispatcher dispatcher;
    DoctorServlet servlet;

    @BeforeEach
    public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        context = Mockito.mock(ServletContext.class);
        dispatcher = Mockito.mock(RequestDispatcher.class);

        servlet = new DoctorServlet(){      //override getservletcontext to mock
            public ServletContext getServletContext() {
                return context;
            }
        };

        Mockito.when(context.getRequestDispatcher(Mockito.anyString())).thenReturn(dispatcher);

        db = Mockito.mock(DBManager.class);
        setMock(db);
    }

    private void setMock(DBManager mock) {
        try {
            Field instance = DBManager.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDoGetWithoutId() throws ServletException, IOException {
        List<Doctor> doctors = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Doctor doctor = new Doctor();
            doctor.id = i;
            doctor.name = "Doctor" + i;
            doctors.add(doctor);
        }

        Mockito.when(db.getAllDoctors()).thenReturn(doctors);

        servlet.doGet(request, response);
        Mockito.verify(db, Mockito.atLeastOnce()).getAllDoctors();         //get doctors
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("people"), Mockito.eq(doctors));     //sets data
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    private Doctor whenDoctorIsChosenPrep(List<Patient> patients) {
        Doctor doctor = new Doctor();
        doctor.id = 5;
        doctor.name = "Doctor" + 5;

        Mockito.when(db.getDoctorById(5)).thenReturn(doctor);

        for (int i = 1; i <= 5; i++) {          //create list with sick patients, doctors can treat all
            Patient patient = new Patient();
            patient.id = i;
            patient.name = "Name" + i;
            patient.isSick = true;
            patient.sickness = "Sickness";

            if (i == 2 || i == 5) {
                patient.treatment = Patient.TREATMENTS[0];
            } else
                patient.treatment = Patient.TREATMENTS[Patient.ONLY_DOC_TREATMENT];

            patient.sicknessHistory = "";
            patients.add(patient);
        }

        Mockito.when(request.getParameter("id")).thenReturn("5");

        Mockito.when(db.getPatientsToTreat()).thenReturn(patients);

        return doctor;
    }

    private void whenDoctorIsChosenVerify(Doctor doctor, List<Patient> patients) throws ServletException, IOException {
        Mockito.verify(db, Mockito.atLeastOnce()).getDoctorById(5);        //gets specific doctor
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("doctor"), Mockito.eq(doctor));     //sets data
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("patientsToTreat"), Mockito.eq(patients));    //only patients that doctor can treat
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    @Test
    void testDoGetWithId() throws ServletException, IOException {
        List<Patient> patients = new ArrayList<>();

        Doctor doctor = whenDoctorIsChosenPrep(patients);

        servlet.doGet(request, response);

        whenDoctorIsChosenVerify(doctor, patients);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        List<Patient> patients = new ArrayList<>();

        Doctor doctor = whenDoctorIsChosenPrep(patients);

        Patient patient = null;
        for (Patient searchPatient : patients)
            if (searchPatient.id == 5)
                patient = searchPatient;

        String sickness = patient.sickness;

        Mockito.when(request.getParameter("treat_patient_id")).thenReturn("5");
        Mockito.when(db.getPatientById(5)).thenReturn(patient);

        servlet.doPost(request, response);

        Mockito.verify(request, Mockito.atLeastOnce()).getParameter(Mockito.eq("treat_patient_id"));    //treats the patient

        Mockito.verify(db, Mockito.atLeastOnce()).editPatient(Mockito.eq(5), Mockito.eq(null),
                Mockito.eq(false), Mockito.eq(""), Mockito.eq(""), Mockito.eq(sickness));   //edits in db

        assertEquals("", patient.sickness);         //check changes
        assertEquals("", patient.treatment);
        assertEquals(sickness, patient.sicknessHistory);
        assertFalse(patient.isSick);

        whenDoctorIsChosenVerify(doctor, patients);
    }
}