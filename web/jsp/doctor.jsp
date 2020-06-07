<%@ page import="lab2.Doctor" %>
<%@ page import="lab2.Patient" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 07.06.2020
  Time: 17:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Doctor</title>
</head>
<body>
    <%
        Doctor doctor = (Doctor) request.getAttribute("doctor");
        if (doctor != null) {
            out.println("<h1>" + doctor.toString() + "</h1>");

            List<Patient> patientsToTreat = (List<Patient>) request.getAttribute("patientsToTreat");
            List<Patient> sickPatients = (List<Patient>) request.getAttribute("sickPatients");

            for (Patient patient : sickPatients) {
                out.println("<form method='post'>");
                out.println("<p>" + patient.toString() + "</p>");
                out.println("<input type='hidden' name='diagnose_patient_id' value='" + patient.id + "'>");
                out.println("Diagnose:");
                out.println("<input type='text' name='diagnose'>");
                out.println("<button type='submit'>Diagnose</button>");
                out.println("</form>");
            }

            out.println("<br><br>");

            for (Patient patient : patientsToTreat) {
                out.println("<form method='post'>");
                out.println("<p>" + patient.toString() + "</p>");
                out.println("<input type='hidden' name='treat_patient_id' value='" + patient.id + "'>");
                out.println("<button type='submit'>Treat</button>");
                out.println("</form>");
            }
        } else {
            out.println("Doctor not found");
        }
    %>

    <a href="./">Go back</a>
</body>
</html>
