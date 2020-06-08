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

class NurseServletTest {
    DBManager db;
    HttpServletRequest request;
    HttpServletResponse response;
    ServletContext context;
    RequestDispatcher dispatcher;
    NurseServlet servlet;

    @BeforeEach
    public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        context = Mockito.mock(ServletContext.class);
        dispatcher = Mockito.mock(RequestDispatcher.class);

        servlet = new NurseServlet(){      //override getservletcontext to mock
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
        List<Nurse> nurses = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Nurse nurse = new Nurse();
            nurse.id = i;
            nurse.name = "Nurse" + i;
            nurses.add(nurse);
        }

        Mockito.when(db.getAllNurses()).thenReturn(nurses);

        servlet.doGet(request, response);
        Mockito.verify(db, Mockito.atLeastOnce()).getAllNurses();         //get nurses
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("people"), Mockito.eq(nurses));     //sets data
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    private Nurse whenNurseIsChosenPrep(List<Patient> patients, List<Patient> patientsToTreat) {
        Nurse nurse = new Nurse();
        nurse.id = 5;
        nurse.name = "Nurse" + 5;

        Mockito.when(db.getNurseById(5)).thenReturn(nurse);

        for (int i = 1; i <= 5; i++) {          //create list with sick patients, some exclusive for treatment for doctors
            Patient patient = new Patient();
            patient.id = i;
            patient.name = "Name" + i;
            patient.isSick = true;
            patient.sickness = "Sickness";

            if (i == 2 || i == 5) {
                patient.treatment = Patient.TREATMENTS[0];
                patientsToTreat.add(patient);               //if not exclusive to doctors, nurse can treat them
            } else
                patient.treatment = Patient.TREATMENTS[Patient.ONLY_DOC_TREATMENT];

            patient.sicknessHistory = "";
            patients.add(patient);
        }

        Mockito.when(request.getParameter("id")).thenReturn("5");

        Mockito.when(db.getPatientsToTreat()).thenReturn(patients);

        return nurse;
    }

    private void whenNurseIsChosenVerify(Nurse nurse, List<Patient> patientsToTreat) throws ServletException, IOException {
        Mockito.verify(db, Mockito.atLeastOnce()).getNurseById(5);        //gets specific nurse
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("nurse"), Mockito.eq(nurse));     //sets data
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("patientsToTreat"), Mockito.eq(patientsToTreat));    //only patients that nurse can treat
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    @Test
    void testDoGetWithId() throws ServletException, IOException {
        List<Patient> patients = new ArrayList<>();
        List<Patient> patientsToTreat = new ArrayList<>();

        Nurse nurse = whenNurseIsChosenPrep(patients, patientsToTreat);

        servlet.doGet(request, response);

        whenNurseIsChosenVerify(nurse, patientsToTreat);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        List<Patient> patients = new ArrayList<>();
        List<Patient> patientsToTreat = new ArrayList<>();

        Nurse nurse = whenNurseIsChosenPrep(patients, patientsToTreat);

        Patient patient = null;
        for (Patient searchPatient : patientsToTreat)
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

        whenNurseIsChosenVerify(nurse, patientsToTreat);
    }
}