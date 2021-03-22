import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * This servlet clears all attributes except the "keypair"
 * This servlet is called when the user logs out.
 * @author Travis Higgins
 */
@WebServlet("/ClearAttributes")
public class ClearAttributes extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Removes all session attributes except "keypair" when the user logs out
            HttpSession session = request.getSession();
            Enumeration<String> attributeNames = session.getAttributeNames();
            for (String name : Collections.list(attributeNames)) {
                if (!name.equals("keypair")) {
                    session.removeAttribute(name);
                }
            }
            // Redirects to index page after session objects have been cleared
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        }
        catch (Exception se) {
            se.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
