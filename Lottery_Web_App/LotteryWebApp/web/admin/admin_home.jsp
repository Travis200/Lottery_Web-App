<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin</title>
</head>
<body bgcolor="yellow">
<%--This will output a message that states the user has successfully logged in or created an account--%>
<p id=message><b><%= request.getAttribute("message")%></b></p><br>
<%--This will display the users information of the account we are logged into--%>
<p>
    <b>Name: </b><%= session.getAttribute("firstName")%> <%= session.getAttribute("lastName")%> <br>
    <b>Username: </b><%= session.getAttribute("username")%> <br>
    <b>Email: </b><%= session.getAttribute("email")%><br>
    <b>Phone: </b><%= session.getAttribute("phone")%><br>
</p><br>
<%--When this form is submitted the page will show a table of all the users data--%>
<form action="GetAdminData" name ="GetAdminData" method = "post">
    <button type = "submit">Get All User Data</button>
</form>
<%--This shows all of the users data--%>
<p id=userData><%=request.getAttribute("userData")%></p><br><br>
<form action="ClearAttributes" name = "logOut" method = "post">
    <%--When this form is submitted the user will be securely logged out and all attributes cleared except keypair--%>
    <button type = "submit">Log Out</button>
</form>
<script>
    // This ensures that if the session attribute is null that this is not displayed
    const user_data = document.getElementById("userData");
    const message = document.getElementById("message");
    if (user_data.innerText === "null"){
        user_data.style.display = "none";
    }
    if (message.innerText === "null"){
        message.style.display = "none"
    }
</script>
</body>
</html>
