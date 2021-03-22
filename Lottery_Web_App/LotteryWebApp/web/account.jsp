<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
</head>
<body bgcolor = "yellow">
<h1>User Account</h1>
<%--This will output a message that states the user has successfully logged in or created an account--%>
<b><p id = message><%=request.getAttribute("message")%><p/></b>
<%--This will display the users information of the account we are logged into--%>
<p>
    <b>Name: </b><%= session.getAttribute("firstName")%> <%= session.getAttribute("lastName")%> <br>
    <b>Username: </b><%= session.getAttribute("username")%> <br>
    <b>Email: </b><%= session.getAttribute("email")%><br>
    <b>Phone: </b><%= session.getAttribute("phone")%><br>
</p><br>
<%--This is where the user chooses their numbers for a draw--%>
    <form onSubmit = "return validateUserNumbers()" action=AddUserNumbers name = "pickNumbers" method = "post">
        <label for="num1">Number 1:</label><br>
        <input type="number" id="num1" name="num1" required><br>
        <label for="num2">Number 2:</label><br>
        <input type="number" id="num2" name="num2" required><br>
        <label for="num3">Number 3:</label><br>
        <input type="number" id="num3" name="num3" required><br>
        <label for="num4">Number 4:</label><br>
        <input type="number" id="num4" name="num4" required><br>
        <label for="num5">Number 5:</label><br>
        <input type="number" id="num5" name="num5" required><br>
        <label for="num6">Number 6:</label><br>
        <input type="number" id="num6" name="num6" required><br><br>
        <button type="submit">Submit</button>
    </form>
<%--This will populate the pickNumbers form with random numbers--%>
<button onclick="fillRandomNum()">Generate Random Numbers</button><br><br>

<form action = "GetUserNumbers" name = "getUserNum" method = "post">
    <button type = "submit">Get Draws</button>
</form>

<form action = "CheckNumbers" name = "checkForMatch" method = "post">
    <button type = "submit">Check for a winning match</button>
</form>
<p>
    <b>Your numbers are: </b>
<%--This will iterate through the string array containing the user draws and print each one to the webpage--%>
    <%
        String[] draws = (String[]) request.getAttribute("draws");
        for (int i = 0; i < draws.length; i++) {
            if (draws[i] != null) {
                out.print("<br>" + draws[i]);
            }
        }
    %>
</p>
<br>
<%--This will output a message that states the user has a winning match or not--%>
<b><p id = wonOrLost><%=request.getAttribute("wonOrLost")%></p></b>
<%--When this form is submitted the user will be securely logged out and all attributes cleared except keypair--%>
<form action="ClearAttributes" name = "logOut" method = "post">
    <button type = "submit">Log Out</button>
</form>

<script>
    // This ensures that if the session attribute is null that this is not displayed
    const message = document.getElementById("message");
    const won_or_lost = document.getElementById("wonOrLost");
    if (message.innerText==="null") {
        message.style.display = "none";
    }
    if (won_or_lost.innerText==="null"){
        won_or_lost.style.display = "none";
    }

    /**
     * validates that the users selected draws are between 0-60 (inclusive) and are also whole integers
     * @returns {boolean} True is returned if the numbers are valid, if not false is returned.
     */
    function validateUserNumbers() {
        const num1 = document.getElementById("num1").value;
        const num2 = document.getElementById("num2").value;
        const num3 = document.getElementById("num3").value;
        const num4 = document.getElementById("num4").value;
        const num5 = document.getElementById("num5").value;
        const num6 = document.getElementById("num6").value;

        if ((num1<0||num1>60)||(num2<0||num2>60)||(num3<0||num3>60)||(num4<0||num4>60)||(num5<0||num5>60)||(num6<0||num6>60)){
            alert("Please only enter numbers between 0 and 60 (including 0 and 60)");
            return false;
        }
        else if (num1 % 1 !== 0 || num2% 1 !== 0 || num3 % 1 !== 0 || num4 % 1 !== 0 || num5 % 1 !== 0 || num6 % 1 !== 0) {
            alert("Please only enter whole numbers");
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * This populates the HTML form for the users draws with random numbers between 0-60 (inclusive).
     */
    function fillRandomNum() {
        let array = new Uint8Array(6);
        window.crypto.getRandomValues(array);
        let randomNum;
        let fieldIds = ['num1', 'num2', 'num3', 'num4', 'num5', 'num6'];
        for (let i = 0; i < array.length; i++) {
            randomNum = array[i]%61;
                document.getElementById(fieldIds[i]).value = randomNum;
        }
    }
</script>
</body>
</html>
