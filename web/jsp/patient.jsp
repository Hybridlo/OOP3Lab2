<%@ page import="lab2.Patient" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Patient</title>
</head>
<body>
    <%
        Patient patient = (Patient) request.getAttribute("patient");
        if (patient != null) {
            out.println("<h1>" + patient.toString() + "</h1>");

            if (!patient.isSick) {
                out.println("<form method='post'>");
                out.println("<button type='submit'>Make sick</button>");
                out.println("</form>");
            }
        } else {
            out.println("Patient not found");
        }
    %>

    <a href="./">Go back</a>
</body>
</html>
