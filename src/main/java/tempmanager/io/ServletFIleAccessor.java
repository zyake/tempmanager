package tempmanager.io;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServletFIleAccessor {

    public static Properties readProperties(ServletContext context, String contextPath) {
        InputStream stream = context.getResourceAsStream(contextPath);
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
