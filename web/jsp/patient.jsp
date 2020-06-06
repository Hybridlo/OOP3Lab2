<%@ page import="lab2.Patient" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 07.06.2020
  Time: 0:08
  To change this template use File | Settings | File Templates.
--%>
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
                out.println("<input type='submit' value='Make sick'>");
                out.println("</form>");
            }
        } else {
            out.println("Patient not found");
        }
    %>
</body>
</html>
