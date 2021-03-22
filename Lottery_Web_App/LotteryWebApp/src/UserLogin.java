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
 * This servlet checks the users login information against the SQL database and directs them to the account.jsp page
 * if their details are correct.
 */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
    Methods methods = new Methods();
    private Connection conn;
    private Statement stmt;
    // This is the amount of login attempts the user is allowed until they are locked out
    int loginAttemptsRemaining = 3;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        try {
            // Create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            // Query database and get results
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String passwordHash = methods.hashCreator(password);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM userAccounts WHERE Username = ?");
            stmt.setString(1, username);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Create string variables for the user data
            String firstName = "";
            String lastName = "";
            String email = "";
            String phone = "";
            String adminStatus = "";
            boolean passCorrect = false;
            //  Checks the password matches and if so the the user data is retrieved and assigned to their
            //  corresponding variables
            while (rs.next()) {
                if (rs.getString("Pwd").equals(passwordHash)) {
                    passCorrect = true;
                    firstName = rs.getString("Firstname");
                    lastName = rs.getString("Lastname");
                    email = rs.getString("Email");
                    phone = rs.getString("Phone");
                    adminStatus = rs.getString("Admin");
                    break;
                }
            }
            if ((!passCorrect)){
                throw new Exception();
            }

            // close connection
            conn.close();

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
            // Reassign value of login attempts to 3 in case they failed to log in on the first time
            loginAttemptsRemaining = 3;
            // display admin_home.jsp page if login successful and if user is an admin
            if (adminStatus.equals("true")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("admin/admin_home.jsp");
                request.setAttribute("message", "login successful");
                // Set an empty array to the attribute draws to prevent a null pointer error
                String[] emptyDraws = new String[0];
                request.setAttribute("draws", emptyDraws);
                dispatcher.forward(request, response);
            }
            // display account.jsp page if login successful and if user is an not admin
            else {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("message", "login successful");
                // Set an empty array to the session attribute draws to prevent a null pointer error
                String[] emptyDraws = new String[0];
                request.setAttribute("draws", emptyDraws);
                dispatcher.forward(request, response);
            }
        }
        catch (Exception se) {
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", "login unsuccessful");
            HttpSession session = request.getSession();
            // Decrement amount of login attempts remaining by one and set this value to a session attribute
            loginAttemptsRemaining --;
            session.setAttribute("loginAttemptsRemaining", loginAttemptsRemaining);
            dispatcher.forward(request, response);
        }

        finally {
            try {
                if (stmt != null)
                    stmt.close();
            }
            catch (SQLException se2) {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
