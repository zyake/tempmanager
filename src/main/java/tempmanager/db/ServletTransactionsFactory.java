package tempmanager.db;

import org.apache.commons.dbcp.BasicDataSource;
import tempmanager.io.SecureDataAccessor;
import tempmanager.io.ServletFIleAccessor;

import javax.servlet.ServletContext;
import java.util.Properties;

public class ServletTransactionsFactory {

    public Transactions createTransactions(ServletContext context, String dataFile) {
        Properties properties = new ServletFIleAccessor().readProperties(context,"/WEB-INF/classes/" + dataFile);
        try {
            Class.forName(properties.getProperty("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("class"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("user"));

        SecureDataAccessor dataAccessor = new SecureDataAccessor(context.getResourceAsStream("/WEB-INF/classes/keystore.ks"));
        String password = dataAccessor.decrypt(properties.getProperty("pass"));
        dataSource.setPassword(password);

        dataSource.setDefaultAutoCommit(false);
        dataSource.setInitialSize(4);
        dataSource.setConnectionProperties("sslmode=require;"); //loggerLevel=TRACE;loggerFile=pgjdbc.log");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(5);

        return new Transactions(dataSource);
    }
}
