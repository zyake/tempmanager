package tempmanager.db;

import org.apache.commons.dbcp.BasicDataSource;
import tempmanager.io.ServletFIleAccessor;

import javax.servlet.ServletContext;
import java.util.Properties;

public class ServletTransactionsFactory {

    public Transactions createTransactions(ServletContext context) {
        Properties properties = new ServletFIleAccessor().readProperties(context,"/WEB-INF/classes/jdbc.properties");
        try {
            Class.forName(properties.getProperty("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(properties.getProperty("class"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("user"));
        dataSource.setPassword(properties.getProperty("pass"));
        dataSource.setDefaultAutoCommit(false);
        dataSource.setInitialSize(4);
        dataSource.setConnectionProperties("sslmode=require;"); //loggerLevel=TRACE;loggerFile=pgjdbc.log");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(5);

        return new Transactions(dataSource);
    }
}
