<%@ page import="lab2.Patient" %>
<%@ page import="java.util.List" %>
<%@ page import="lab2.Nurse" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 07.06.2020
  Time: 16:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Nurse</title>
</head>
<body>
    <%
        Nurse nurse = (Nurse) request.getAttribute("nurse");
        if (nurse != null) {
            out.println("<h1>" + nurse.toString() + "</h1>");

            List<Patient> patientsToTreat = (List<Patient>) request.getAttribute("patientsToTreat");

            for (Patient patient : patientsToTreat) {
                out.println("<form method='post'>");
                out.println("<p>" + patient.toString() + "</p>");
                out.println("<input type='hidden' name='treat_patient_id' value='" + patient.id + "'>");
                out.println("<button type='submit'>Treat</button>");
                out.println("</form>");
            }

        } else {
            out.println("Nurse not found");
        }
    %>

    <a href="./">Go back</a>
</body>
</html>
