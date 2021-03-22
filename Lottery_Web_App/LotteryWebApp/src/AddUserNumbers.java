import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.*;

/**
 * This servlet takes the users set of numbers for a draw, encrypts them and saves the encrypted draw to a text file.
 * @author Travis Higgins
 */
@WebServlet("/AddUserNumbers")
public class AddUserNumbers extends HttpServlet {
    Methods methods = new Methods();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String num1 = request.getParameter("num1");
            String num2 = request.getParameter("num2");
            String num3 = request.getParameter("num3");
            String num4 = request.getParameter("num4");
            String num5 = request.getParameter("num5");
            String num6 = request.getParameter("num6");
            HttpSession session = request.getSession();
            KeyPair existingPair = (KeyPair) session.getAttribute("keypair");
            KeyPair pair;
            // Generates a keypair only if one does not already exist
            if (existingPair == null) {
                KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
                pair = keyPairGen.generateKeyPair();
                session.setAttribute("keypair", pair);
            }
            else {
                pair = existingPair;
            }
            String userNumbers = num1 + "," + num2 + "," + num3 + "," + num4 + "," + num5 + "," + num6;
            byte[] encryptedNum = methods.encryptDraw(userNumbers, pair);
            String passwordHash = (String) session.getAttribute("passwordHash");
            String passwordHash20Char = passwordHash.substring(0,20);
            methods.writeCipherTextToFile(passwordHash20Char, encryptedNum);
            // Set an empty array to the attribute draws to prevent a null pointer error
            String[] emptyDraws = new String[0];
            request.setAttribute("draws", emptyDraws);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
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
