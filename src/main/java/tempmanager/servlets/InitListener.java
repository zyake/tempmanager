package tempmanager.servlets;

import org.apache.commons.dbcp.BasicDataSource;
import tempmanager.db.BasicQueryExecutorFactory;
import tempmanager.db.Transactions;
import tempmanager.models.TempratureRepository;
import tempmanager.services.TempratureService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        Transactions trns = getBasicTransactions(servletContext);
        String sqlDirPath = servletContext.getRealPath("/WEB-INF/sqls");
        TempratureRepository tempratureRepository = new TempratureRepository(trns.getQueryExecutor(sqlDirPath, BasicQueryExecutorFactory.DEFAULT_EXCEPTION_HANDLER));
        TempratureService statusService = new TempratureService(tempratureRepository);

        servletContext.addServlet("status", new StatusServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/status");

        servletContext.addServlet("record", new RecordTempratureServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/record");
    }

    private Transactions getBasicTransactions(ServletContext servletContext) {
        InputStream resourceAsStream = servletContext.getResourceAsStream("/WEB-INF/jdbc.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(5);

        return new Transactions(dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
