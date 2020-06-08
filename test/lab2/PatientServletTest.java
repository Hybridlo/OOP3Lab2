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

class PatientServletTest {
    DBManager db;
    HttpServletRequest request;
    HttpServletResponse response;
    ServletContext context;
    RequestDispatcher dispatcher;
    PatientServlet servlet;

    @BeforeEach
    public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        context = Mockito.mock(ServletContext.class);
        dispatcher = Mockito.mock(RequestDispatcher.class);

        servlet = new PatientServlet(){      //override getservletcontext to mock
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
        List<Patient> patients = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Patient patient = new Patient();
            patient.id = i;
            patient.name = "Name" + i;
            patient.isSick = false;
            patient.sickness = "";
            patient.treatment = "";
            patient.sicknessHistory = "";
            patients.add(patient);
        }

        Mockito.when(db.getAllPatients()).thenReturn(patients);

        servlet.doGet(request, response);
        Mockito.verify(db, Mockito.atLeastOnce()).getAllPatients();         //get patients
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("people"), Mockito.eq(patients));     //sets data
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    private Patient whenPatientIsChosenPrep() {
        Patient patient = new Patient();
        patient.id = 5;
        patient.name = "Name" + 5;
        patient.isSick = false;
        patient.sickness = "";
        patient.treatment = "";
        patient.sicknessHistory = "";

        Mockito.when(db.getPatientById(5)).thenReturn(patient);

        Mockito.when(request.getParameter("id")).thenReturn("5");

        return patient;
    }

    private void whenPatientIsChosenVerify(Patient patient) throws ServletException, IOException {
        Mockito.verify(db, Mockito.atLeastOnce()).getPatientById(5);        //gets specific patient
        Mockito.verify(request, Mockito.atLeastOnce()).setAttribute(Mockito.eq("patient"), Mockito.eq(patient));     //sets data
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).forward(request, response);       //forwards to jsp
    }

    @Test
    void testDoGetWithId() throws ServletException, IOException {
        Patient patient = whenPatientIsChosenPrep();

        servlet.doGet(request, response);

        whenPatientIsChosenVerify(patient);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        Patient patient = whenPatientIsChosenPrep();

        servlet.doPost(request, response);

        Mockito.verify(db, Mockito.atLeastOnce()).editPatient(Mockito.eq(5), Mockito.eq(null),
                Mockito.eq(true), Mockito.eq(null), Mockito.eq(null), Mockito.eq(null));    //edits patient

        assertTrue(patient.isSick);         //check changes
        whenPatientIsChosenVerify(patient);
    }
}