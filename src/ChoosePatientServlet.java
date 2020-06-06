import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class ChoosePatientServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {

            response.setContentType("text/html;charset=UTF-8");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Index</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>Patients</h1>");
            out.println("Type patient id:");
            out.println("<input id=\"patientNum\" type=\"number\" required>");

            out.println("<button onclick=\"window.location.href+=\'/\'+document.getElementById(\'patientNum\').value\">" +
                    "Choose</button>");

            out.println("<h3>All patients:</h3>");

            DBManager db = DBManager.getInstance();

            for (Patient patient : db.getAllPatients())
                out.println("<p>id: " + patient.id + " name: " + patient.name);

            out.println("</body>");
            out.println("</html>");
        } catch (SQLException | ClassNotFoundException ignore) {
        }
    }

    public String getServletInfo() {
        return "ChoosePatient";
    }
}
