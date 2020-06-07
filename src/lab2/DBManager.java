package lab2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private Connection con;
    private Statement stmt;
    private static DBManager instance;

    private DBManager() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://localhost/hospital", "postgres", "5502");
        stmt = con.createStatement();
    }

    public static synchronized DBManager getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null){
            instance = new DBManager();
        }

        return instance;
    }

    public Statement getStatement() {
        return stmt;
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT * " +
                "FROM patient P1 " +
                "WHERE P1.id = " + id;

        Patient patient = new Patient();
        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                patient.id = id;
                patient.name = rs.getString("name");
                patient.isSick = rs.getBoolean("isSick");
                patient.sickness = rs.getString("sickness");
                patient.treatment = rs.getString("treatment");
                patient.sicknessHistory = rs.getString("sickness_history");
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }

        return patient;
    }

    public Nurse getNurseById(int id) {
        String sql = "SELECT * " +
                "FROM nurse N1 " +
                "WHERE N1.id = " + id;

        Nurse nurse = new Nurse();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                nurse.id = id;
                nurse.name = rs.getString("name");
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }

        return nurse;
    }

    public Doctor getDoctorById(int id) {
        String sql = "SELECT * " +
                "FROM doctor D1 " +
                "WHERE D1.id = " + id;

        Doctor doctor = new Doctor();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                doctor.id = id;
                doctor.name = rs.getString("name");
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }

        return doctor;
    }

    public boolean editPatient(int id, String name, Boolean isSick, String sickness, String treatment, String sicknessHistory) {
        String sql = "UPDATE patient " +
                "SET ";

        if (name == null && isSick == null && sickness == null && treatment == null)
            return false;

        boolean set = false;
        if (name != null) {
            sql += "name = '" + name + "'";
            set = true;
        }

        if (isSick != null) {
            if (set) sql += ", ";

            sql += "isSick = " + isSick;
            set = true;
        }

        if (sickness != null) {
            if (set) sql += ", ";

            sql += "sickness = '" + sickness + "'";
            set = true;
        }

        if (treatment != null) {
            if (set) sql += ", ";

            sql += "treatment = '" + treatment + "'";
            set = true;
        }

        if (sicknessHistory != null) {
            if (set) sql += ", ";

            sql += "sickness_history = '" + sicknessHistory + "'";
        }

        sql += " WHERE id = " + id;

        try
        {
            int c = stmt.executeUpdate(sql);

            return c > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addPatient(String name, boolean isSick, String sickness, String treatment, String sicknessHistory) {
        String sql = "INSERT INTO patient " +
                "(name, isSick, sickness, treatment, sickness_history) " +
                "VALUES ('" + name + "', " + isSick + ", '" + sickness + "', '" + treatment + "', '" + sicknessHistory + "')";

        try
        {
            int c = stmt.executeUpdate(sql);

            return c > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Patient> getAllPatients() {
        String sql = "SELECT id, * FROM patient";

        List<Patient> patients = new ArrayList<>();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Patient patient = new Patient();
                patient.id = rs.getInt("id");
                patient.name = rs.getString("name");
                patient.isSick = rs.getBoolean("isSick");
                patient.sickness = rs.getString("sickness");
                patient.treatment = rs.getString("treatment");
                patient.sicknessHistory = rs.getString("sickness_history");
                patients.add(patient);
            }
            rs.close();

        } catch (SQLException e) {
            return null;
        }

        return patients;
    }

    public List<Nurse> getAllNurses() {
        String sql = "SELECT id, * FROM nurse";

        List<Nurse> nurses = new ArrayList<>();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Nurse nurse = new Nurse();
                nurse.id = rs.getInt("id");
                nurse.name = rs.getString("name");
                nurses.add(nurse);
            }
            rs.close();

        } catch (SQLException e) {
            return null;
        }

        return nurses;
    }

    public List<Doctor> getAllDoctors() {
        String sql = "SELECT id, * FROM doctor";

        List<Doctor> doctors = new ArrayList<>();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Doctor doctor = new Doctor();
                doctor.id = rs.getInt("id");
                doctor.name = rs.getString("name");
                doctors.add(doctor);
            }
            rs.close();

        } catch (SQLException e) {
            return null;
        }

        return doctors;
    }

    public List<Patient> getSickPatients() {
        String sql = "SELECT id, * FROM patient P1" +
                " WHERE P1.isSick = true";

        List<Patient> patients = new ArrayList<>();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Patient patient = new Patient();
                patient.id = rs.getInt("id");
                patient.name = rs.getString("name");
                patient.isSick = rs.getBoolean("isSick");
                patient.sickness = rs.getString("sickness");
                patient.treatment = rs.getString("treatment");
                patient.sicknessHistory = rs.getString("sickness_history");
                patients.add(patient);
            }
            rs.close();

        } catch (SQLException e) {
            return null;
        }

        return patients;
    }

    public List<Patient> getPatientsToTreat() {
        String sql = "SELECT id, * FROM patient P1 " +
                "WHERE CHAR_LENGTH(P1.treatment) > 0";

        List<Patient> patients = new ArrayList<>();

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Patient patient = new Patient();
                patient.id = rs.getInt("id");
                patient.name = rs.getString("name");
                patient.isSick = rs.getBoolean("isSick");
                patient.sickness = rs.getString("sickness");
                patient.treatment = rs.getString("treatment");
                patient.sicknessHistory = rs.getString("sickness_history");
                patients.add(patient);
            }
            rs.close();

        } catch (SQLException e) {
            return null;
        }

        return patients;
    }
}
