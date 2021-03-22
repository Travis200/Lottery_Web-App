<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body bgcolor = "yellow">
<h1>Error Page</h1>
<%--Displays a message dependant on the type of error--%>
<p><b><%= request.getAttribute("message") %></b></p><br>
<a href = index.jsp>Home</a>
<%--This redirects the user to the home page and clears all attributes except the keypair--%>
<%--   <form action="ClearAttributes" name = "logOut" method = "post">--%>
<%--       <button type = "submit">Back To Home</button>--%>
<%--   </form>--%>
</body>
</html>
