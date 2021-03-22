import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * This filter ensures no forbidden values can be sent through any of the HTML forms.
 * @author Travis Higgins
 */
@WebFilter(filterName = "CheckForSQLFilter")
public class CheckForSQLFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
        config.getServletContext().log("Filter Started");
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        boolean invalid = false;
        Map params = request.getParameterMap();

        if(params != null){
            Iterator iter = params.keySet().iterator();
            while(iter.hasNext()){
                String key = (String) iter.next();
                String[] values = (String[]) params.get(key);

                for(int i=0; i < values.length; i++){
                    if(checkChars(values[i])){
                        invalid = true;
                        break;
                    }
                }
                if (invalid) {break;}
            }
        }
        if(invalid){
            try{
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                request.setAttribute("message",  "Form submitted containing forbidden values! Do not submit " +
                        "data containing <,  >,  !,  {,  } , insert, into, where, script, delete, input");
                dispatcher.forward(request, response);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            chain.doFilter(request, response);
        }
    }

    /** This is used to that the String does not contain any forbidden values
     * @param value String to be checked for forbidden values
     * @return A boolean value where true means the data is valid and false means the data is invalid
     */
    public static boolean checkChars(String value) {
        boolean invalid = false;
        // These characters are not allowed to help prevent SQL injection
        String[] badChars = {"<", ">", "!", "{", "}", "insert", "into", "where", "script", "delete", "input"};;
        for(int i = 0; i < badChars.length; i++){
            if(value.indexOf(badChars[i]) >=0){
                invalid = true;
                break;
            }
        }
        return invalid;
    }
}
