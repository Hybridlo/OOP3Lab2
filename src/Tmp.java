import java.sql.SQLException;

public class Tmp {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBManager db = DBManager.getInstance();

        for (Patient patient : db.getAllPatients())
            System.out.println(patient.id);
    }
}
