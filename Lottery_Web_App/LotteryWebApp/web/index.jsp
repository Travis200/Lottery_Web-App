<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home</title>
</head>
<body bgcolor = "yellow">
</div>

<h1>Register</h1>
<%--This is the form where the user enters there registration information--%>
<form onSubmit = "return validateRegister()" action="CreateAccount" name = "registration" method="post">
    <label for="firstName">First name:</label><br>
    <input type="text" id="firstName" name="firstName" style="text-transform:capitalize"><br>
    <label for="lastName">Last name:</label><br>
    <input type="text" id="lastName" name="lastName" style="text-transform:capitalize"><br>
    <label for="username">Username:</label><br>
    <input type="text" id="username" name="username"><br>
    <label for="phone">Phone:</label><br>
    <input type="tel" id="phone" name="phone"  placeholder="44-0191-1234567"><br>
    <label for="email">Email:</label><br>
    <input type="email" id="email" name="email"  placeholder="joebloggs@email.com" style="text-transform:lowercase"><br>
    <label for="password">Password:</label><br>
    <input type="password" id="password" name="password"  placeholder="Password123"><br><br>
    <input type='checkbox' id="adminBool" name='adminBool' value='true'>
    <label for="adminBool"> I would like to make an admin account </label><br>
    <button type="submit">Submit</button>
</form><br><br><br>

<h1>Log in</h1>
<%--This is them form where the user enters their log in information--%>
<form id = "userLoginForm" onSubmit = "return validateLogin()" action="UserLogin" name = "login" method="post">
    <label for="username1">Username:</label><br>
    <input type="text" id="username1" name="username"><br>
    <label for="password1">Password:</label><br>
    <input type="password" id="password1" name="password"><br><br>
    <input type = hidden id = "attempts" name = "attempts">
    <button type="submit">Log in</button>
</form>
<%--This shows the user how many log in attempts they have remaining until they are locked out--%>
<p id="attemptsRemaining"></p>
<script>
    <%--      This sets the a amoung of login attempts remaining to 3 if there has not yet been a failed attempt--%>
    let loginAttempts =<%out.print(session.getAttribute("loginAttemptsRemaining"));%>;
    if (loginAttempts === null){
        loginAttempts = 3;
    }
    //Print to the page how many attempts are remaining
    else {
        document.getElementById("attemptsRemaining").innerHTML = "You have " + loginAttempts + " log in " +
            "attempts remaining.";
    }

    /**
     * This checks all the registration data meets certain requirements.
     * If the data is invalid an alert tells the user what data is invalid.
     * @returns {boolean} True is returned if the data. False if not.
     */
    function validateRegister() {
        const password = document.getElementById("password").value;
        const email = document.getElementById("email").value;
        const phone = document.getElementById("phone").value;
        const firstName = document.getElementById("firstName").value;
        const lastName = document.getElementById("lastName").value;
        const username = document.getElementById("username").value;
        let dataValid = Boolean(true);
        // Performs an initial check to ensure the user has entered data into every field in the registration form
        if (username === "" || email === "" || firstName === "" || lastName === "" || password === "") {
            alert("Please ensure there are no blank fields");
            return false;
        }
        if (!(firstName.match(/^[A-Za-z\-]+$/) )||(!(lastName.match(/^[A-Za-z\-]+$/)))) {
            alert("Please ensure only letters are used in the first-name and last-name fields");
            dataValid = false;
        }
        if (!(username.match(/[A-Za-z\d-]$/))) {
            alert("Please only use letter, numbers and - in the username");
            dataValid = false;
        }
        if (!(phone.match(/\d{2}-\d{4}-\d{7}$/))) {
            alert("Telephone is not valid, please enter a valid phone number of the form XX--XXXX-XXXXXXX");
            dataValid = false;
        }
        if (!(email.match(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/))) {
            alert("Email address is not valid, please enter a valid email, e.g. example@email.com");
            dataValid = false;
        }
        if (!(password.match(/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,15}$/))) {
            alert("Password is not valid, password must contain: \n" +
                "Between 8 and 15 characters\n" +
                "At least 1 uppercase and 1 lowercase character\n" +
                "At least 1 digit");
            dataValid = false;
        }
        return dataValid;
    }

    /**
     * This checks all the log in data meets certain requirements.
     * If the data is invalid an alert tells the user what data is invalid.
     * @returns {boolean} True is returned if the data is valid and the user is not out of log in attempts. False if not.
     */
    function validateLogin() {
        var username = document.getElementById("username1").value;
        var password = document.getElementById("password1").value;

        if (!(username.match(/[A-Za-z\d-]$/))) {
            alert("Invalid username / password combination");
            return false;
        }
        else if (!(password.match(/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,15}$/))) {
            alert("Invalid username / password combination");
            return false;
        }
        else {
            return limitLoginAttempts();
        }
    }

    /**
     * This function ensures the user is allowed no more than three consecutive failed log in attempts.
     * @returns {boolean} True is returned if user isn allowed another log in attempt. False if not.
     */
    function limitLoginAttempts() {
        if (loginAttempts <= 0) {
            alert("You have logged in incorrectly too many times. You are now locked out.");
            // Disables the log in fields and form so user cannot enter data into it anymore
            document.getElementById("username1").disabled = true;
            document.getElementById("password1").disabled = true;
            document.getElementById("userLoginForm").disabled = true;
            return false;
        }
        return true;
    }
</script>
</body>
</html>
