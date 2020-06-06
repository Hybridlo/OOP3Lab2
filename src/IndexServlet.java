import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (PrintWriter out = response.getWriter()) {

            response.setContentType("text/html;charset=UTF-8");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Index</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Index</h1>");

            out.println("<button type=\"button\" onclick=\"window.location.href+='/doctor';\">As doctor</button><br>");
            out.println("<button type=\"button\" onclick=\"window.location.href+='/nurse';\">As nurse</button><br>");
            out.println("<button type=\"button\" onclick=\"window.location.href+='/patient';\">As patient</button><br>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    public String getServletInfo() {
        return "Index";
    }
}