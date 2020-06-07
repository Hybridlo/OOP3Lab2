<%@ page import="java.util.List" %>
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
            out.println("<p style=\"font-size: 18\">" + person.toString() +"</p>");
    %>

    <a href="./">Go back</a>
</body>
</html>
