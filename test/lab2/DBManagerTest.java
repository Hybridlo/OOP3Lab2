package lab2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}