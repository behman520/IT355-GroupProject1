import java.util.logging.Level;
import java.util.logging.Logger;

public class ERR02 {
        public static void main(String[] args) throws Exception {
        final Logger logger = Logger.getLogger(ERR02.class.getName());
        
        try {
        // Code that might trigger a security exception
        } catch (SecurityException se) {
        logger.log(Level.SEVERE, "A security exception occurred", se); 
        }
    }
}
