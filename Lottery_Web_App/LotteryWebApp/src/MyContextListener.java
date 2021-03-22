import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * This class is used to ensure that if the directory encrypted draws exists when a session is closed or when
 * a new session is started, that the directory is deleted.
 * @author Travis Higgins
 */
public class MyContextListener implements ServletContextListener {
    @Override
    // This method ensures that all files generated while the program is running are deleted when the session ends
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        File dir = new File("./EncryptedNumbers");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }

    @Override
    // This method ensures that all files generated from a previous session are deleted when the session starts
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        File dir = new File("./EncryptedNumbers");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }
}
