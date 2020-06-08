package lab2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

class DBManagerTest {
    private DBManager db = DBManager.getInstance();

    DBManagerTest() throws SQLException, ClassNotFoundException { }

    @BeforeEach
    void copyTables() throws SQLException {
        Statement stmt = db.getStatement();
        stmt.execute("ALTER TABLE patient RENAME TO actual_patient");   //rename "production" tables
        stmt.execute("ALTER TABLE nurse RENAME TO actual_nurse");
        stmt.execute("ALTER TABLE doctor RENAME TO actual_doctor");

        stmt.execute("CREATE TABLE patient (LIKE backup_patient INCLUDING ALL)");   //set new tables from "clean" ones
        stmt.execute("CREATE TABLE nurse (LIKE backup_nurse INCLUDING ALL)");
        stmt.execute("CREATE TABLE doctor (LIKE backup_doctor INCLUDING ALL)");
        stmt.execute("INSERT INTO patient SELECT * FROM backup_patient");
        stmt.execute("INSERT INTO nurse SELECT * FROM backup_nurse");
        stmt.execute("INSERT INTO doctor SELECT * FROM backup_doctor");
    }

    @AfterEach
    void revertTables() throws SQLException {
        Statement stmt = db.getStatement();

        stmt.execute("DROP TABLE patient");                              //remove created tables
        stmt.execute("DROP TABLE nurse");
        stmt.execute("DROP TABLE doctor");

        stmt.execute("ALTER TABLE actual_patient RENAME TO patient");   //rename "production" tables back
        stmt.execute("ALTER TABLE actual_nurse RENAME TO nurse");
        stmt.execute("ALTER TABLE actual_doctor RENAME TO doctor");
    }

    @Test
    void testGetPatientById() {
        int id = 10;
        Patient patient = db.getPatientById(id);

        assertEquals(id, patient.id);
        assertEquals("Name10", patient.name);
        assertFalse(patient.isSick);
        assertEquals("", patient.sickness);
        assertEquals("", patient.treatment);
        assertNull(patient.sicknessHistory);            //null because field created afterwards
    }

    @Test
    void testGetNurseById() {
        int id = 2;
        Nurse nurse = db.getNurseById(id);

        assertEquals(id, nurse.id);
        assertEquals("Nurse2", nurse.name);
    }

    @Test
    void testGetDoctorById() {
        int id = 1;
        Doctor doctor = db.getDoctorById(id);

        assertEquals(id, doctor.id);
        assertEquals("Doctor1", doctor.name);
    }

    @Test
    void testEditPatientOneArg() {
        int id = 14;
        db.editPatient(id, null, null, "Yes", null, null);

        Patient patient = db.getPatientById(id);        //if that test works

        assertEquals(id, patient.id);
        assertEquals("Name14", patient.name);
        assertFalse(patient.isSick);
        assertEquals("Yes", patient.sickness);
        assertEquals("", patient.treatment);
        assertNull(patient.sicknessHistory);
    }

    @Test
    void testEditPatientTwoArgs() {
        int id = 25;
        db.editPatient(id, null, null, "Yes", null, "Hello");

        Patient patient = db.getPatientById(id);        //if that test works

        assertEquals(id, patient.id);
        assertEquals("Name5", patient.name);
        assertFalse(patient.isSick);
        assertEquals("Yes", patient.sickness);
        assertEquals("", patient.treatment);
        assertEquals("Hello", patient.sicknessHistory);
    }

    @Test
    void testEditPatientAllArgs() {
        int id = 37;
        db.editPatient(id, "NameNot", true, "VerySick", "WillBeFine", "NotMuch");

        Patient patient = db.getPatientById(id);        //if that test works

        assertEquals(id, patient.id);
        assertEquals("NameNot", patient.name);
        assertTrue(patient.isSick);
        assertEquals("VerySick", patient.sickness);
        assertEquals("WillBeFine", patient.treatment);
        assertEquals("NotMuch", patient.sicknessHistory);
    }

    @Test
    void testAddPatient() throws SQLException {
        assertTrue(db.addPatient("Name100", false, "", "", "WasSick"));

        Statement stmt = db.getStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM patient");
        int id = 0;

        if (rs.next())
            id = rs.getInt("MAX");

        Patient patient = db.getPatientById(id);        //if that test works

        assertEquals(id, patient.id);
        assertEquals("Name100", patient.name);
        assertFalse(patient.isSick);
        assertEquals("", patient.sickness);
        assertEquals("", patient.treatment);
        assertEquals("WasSick", patient.sicknessHistory);
    }

    @Test
    void getAllPatients() {
        List<Patient> patients = db.getAllPatients();

        for (int i = 1; i <= 40; i++) {
            int expPatientId = i;
            String expPatientName = "Name" + ((i > 20) ? i - 20 : i);
            boolean expPatientIsSick = false;
            String expPatientSickness = "";
            String expPatientTreatment = "";
            String expPatientSicknessHistory = null;

            for (Patient patient : patients) {
                if (patient.id == expPatientId) {
                    assertEquals(expPatientId, patient.id);
                    assertEquals(expPatientIsSick, patient.isSick);
                    assertEquals(expPatientName, patient.name);
                    assertEquals(expPatientSickness, patient.sickness);
                    assertEquals(expPatientTreatment, patient.treatment);
                    assertEquals(expPatientSicknessHistory, patient.sicknessHistory);
                    break;
                }
            }
        }
    }

    @Test
    void testGetAllNurses() {
        List<Nurse> nurses = db.getAllNurses();

        for (int i = 1; i <= 3; i++) {
            int expNurseId = i;
            String expNurseName = "Nurse" + i;

            for (Nurse nurse: nurses) {
                if (nurse.id == expNurseId) {
                    assertEquals(expNurseId, nurse.id);
                    assertEquals(expNurseName, nurse.name);
                    break;
                }
            }
        }
    }

    @Test
    void testGetAllDoctors() {
        List<Doctor> doctors = db.getAllDoctors();

        for (int i = 1; i <= 1; i++) {      //one doctor in db
            int expDoctorId = i;
            String expDoctorName = "Doctor" + i;

            for (Doctor doctor: doctors) {
                if (doctor.id == expDoctorId) {
                    assertEquals(expDoctorId, doctor.id);
                    assertEquals(expDoctorName, doctor.name);
                    break;
                }
            }
        }
    }

    @Test
    void testGetSickPatientsEmpty() {
        List<Patient> patients = db.getSickPatients();

        assertEquals(0, patients.size());
    }

    @Test
    void testGetSickPatients() {
        db.editPatient(3, null, true, null, null, null);
        db.editPatient(15, null, true, null, null, null);
        db.editPatient(28, null, true, null, null, null);

        List<Patient> patients = db.getSickPatients();

        assertEquals(3, patients.size());

        for (Patient patient : patients) {
            assertTrue(patient.isSick);
        }
    }

    @Test
    void testGetPatientsToTreatEmpty() {
        List<Patient> patients = db.getPatientsToTreat();

        assertEquals(0, patients.size());
    }

    @Test
    void testGetPatientsToTreat() {
        db.editPatient(3, null, true, null, "A", null);
        db.editPatient(15, null, true, null, "B", null);
        db.editPatient(28, null, true, null, "C", null);

        List<Patient> patients = db.getPatientsToTreat();

        assertEquals(3, patients.size());

        for (Patient patient : patients) {
            assertTrue(patient.treatment != null && !patient.treatment.equals(""));
        }
    }
}