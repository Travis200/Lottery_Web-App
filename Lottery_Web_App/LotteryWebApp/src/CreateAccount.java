import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

/**
 * This servlet adds the account registration information to the SQL database.
 * @author Travis Higgins
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
    Methods methods = new Methods();
    private Connection conn;
    private PreparedStatement stmt;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // MySql database connection info
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";

        // URLs to connect to database depending on your development approach
        // (NOTE: please change to option 1 when submitting)

        // 1. use this when running everything in Docker using docker-compose
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        // 2. use this when running tomcat server locally on your machine and mysql database server in Docker
        //String DB_URL = "jdbc:mysql://localhost:33333/lottery";

        // 3. use this when running tomcat and mysql database servers on your machine
        //String DB_URL = "jdbc:mysql://localhost:3306/lottery";

        // Get parameter data that was submitted in HTML form (use form attributes 'name')
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email").toLowerCase();
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String adminStatus = request.getParameter("adminBool");
        // This ensures firstName and lastName is capitalised correctly
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();

        if(adminStatus == null){
            adminStatus = "false";
        }

        try {
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // This hashes the password for security purposes
            String passwordHash = methods.hashCreator(password);
            // Create sql query
            String query = "INSERT INTO userAccounts (Firstname, Lastname, Email, Phone, Username, Pwd, Admin)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)";
            // set values into SQL query statement
            stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, username);
            stmt.setString(6, passwordHash);
            stmt.setString(7, adminStatus);

            // Set user data to session attributes so they can be displayed on account page
            // Note passwordHash is not displayed on the account page but is used for to name the encrypted files which
            // stores the user draws
            HttpSession session = request.getSession();
            session.setAttribute("firstName", firstName);
            session.setAttribute("lastName", lastName);
            session.setAttribute("username", username);
            session.setAttribute("email", email);
            session.setAttribute("phone", phone);
            session.setAttribute("passwordHash", passwordHash);

            // execute query and close connection
            stmt.execute();
            conn.close();
            // display admin_home.jsp page if account creation is successful and if user is an admin
            if (adminStatus.equals("true")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("admin/admin_home.jsp");
                request.setAttribute("message", firstName + ", you have successfully created an admin account");
                // Set an empty array to the attribute draws to prevent a null pointer error
                String[] emptyDraws = new String[0];
                request.setAttribute("draws", emptyDraws);
                dispatcher.forward(request, response);
            }
            // display account.jsp page if account creation is successful and if user is an not admin
            else {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("message", firstName + ", you have successfully created an account");
                // Set an empty array to the attribute draws to prevent a null pointer error
                String[] emptyDraws = new String[0];
                request.setAttribute("draws", emptyDraws);
                dispatcher.forward(request, response);
            }

        } catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if account creation unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", firstName + ", account registration failed, this username/password " +
                    "combination may already exist. Please try again");
            dispatcher.forward(request, response);
        }
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        doPost(request, response);
    }
}

