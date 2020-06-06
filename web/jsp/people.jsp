<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 06.06.2020
  Time: 18:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>People</title>
    <script src="${pageContext.request.contextPath}/js/withId.js"></script>
</head>
<body>
    <h1>People</h1>
    Type person id:
    <input id="patientNum" type="number">

    <button onclick="withId()">Choose</button>
    <%
        List<Object> patients = (List<Object>) request.getAttribute("people");
        for (Object person : patients)
            out.println("<p>" + person.toString() +"</p>");
    %>
</body>
</html>
