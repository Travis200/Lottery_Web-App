import javax.crypto.Cipher;
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
 * This servlet decrypts the users draw(s) stored in a text file and sends this data as a request to
 * the account.jsp page.
 * @author Travis Higgins
 */
@WebServlet("/GetUserNumbers")
public class GetUserNumbers extends HttpServlet {
    Methods methods = new Methods();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            // Gets cipher and keypair from session attributes
            KeyPair pair = (KeyPair) session.getAttribute("keypair");
            Cipher cipher = (Cipher) session.getAttribute("cipher");
            // Ensures cipher is not null and generates a new one if it is
            if (cipher == null){
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            String passwordHash = (String) session.getAttribute("passwordHash");
            String passwordHash20Char = passwordHash.substring(0,20);
            // Directory of where to retrieve encrypted data from.
            String filename = "./EncryptedNumbers/"+passwordHash20Char+".txt";
            // Decrypted draws and assigns them to a string array
            String[] arrayOfDecryptedData = methods.decryptAllUserNum(filename, pair, cipher);
            // Send the draws as a request attribute so they can be displayed on the account page
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            request.setAttribute("draws", arrayOfDecryptedData);
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
