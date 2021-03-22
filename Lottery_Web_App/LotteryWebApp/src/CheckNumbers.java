import javax.crypto.Cipher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.util.Arrays;

/**
 * This servlet decrypts the users draw(s) stored in a text file and sends this data as a request to
 * the account.jsp page.
 * @author Travis Higgins
 */
@WebServlet("/CheckNumbers")
public class CheckNumbers extends HttpServlet {
    Methods methods = new Methods();
    // These are the numbers the user needs to win the draw
    private int[] winningNumbers = {1,7,9,55,59,33};

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Gets the keypair from session attributes
            HttpSession session = request.getSession();
            KeyPair pair = (KeyPair) session.getAttribute("keypair");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            String passwordHash = (String) session.getAttribute("passwordHash");
            String passwordHash20Char = passwordHash.substring(0,20);
            // Directory of where to decrypt data
            String filepath = "./EncryptedNumbers/"+passwordHash20Char+".txt";
            // Assigns decrypted draws to a string array
            String[] arrayOfDecryptedDraws = methods.decryptAllUserNum(filepath, pair, cipher);
            // Checks if one of users draws matches winning draw
            boolean wonOrLost = methods.checkIfUserWon(winningNumbers, arrayOfDecryptedDraws);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
            if (wonOrLost) {
                request.setAttribute("wonOrLost", "Congratulations, the winning numbers are: "
                        + Arrays.toString(winningNumbers) + ". You have a winning match.");
                String[] x = new String[0];
                request.setAttribute("draws", x);
                dispatcher.forward(request, response);
            }
            else {
                request.setAttribute("wonOrLost", "Unlucky, the winning numbers are: "
                        + Arrays.toString(winningNumbers) + ". You do not have a winning match. Better luck next time.");
                // Set an empty array to the attribute draws to prevent a null pointer error
                String[] emptyDraws = new String[0];
                request.setAttribute("draws", emptyDraws);
                dispatcher.forward(request, response);
            }
            // Clears text file where user draws are stored
            PrintWriter writer = new PrintWriter(filepath);
            writer.print("");
            writer.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
