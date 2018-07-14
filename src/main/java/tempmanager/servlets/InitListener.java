package tempmanager.servlets;

import org.apache.commons.dbcp.BasicDataSource;
import tempmanager.db.BasicQueryExecutorFactory;
import tempmanager.db.QueryExecutor;
import tempmanager.db.Transactions;
import tempmanager.jobs.IndexMaintenanceJob;
import tempmanager.models.TempratureRepository;
import tempmanager.services.TempratureService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

@WebListener
public class InitListener implements ServletContextListener {

    private final Timer timer = new Timer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        Transactions trns = getBasicTransactions(servletContext);
        String sqlDirPath = servletContext.getRealPath("/WEB-INF/sqls");
        QueryExecutor queryExecutor = trns.getQueryExecutor(sqlDirPath, BasicQueryExecutorFactory.DEFAULT_EXCEPTION_HANDLER);

        TempratureRepository tempratureRepository = new TempratureRepository(queryExecutor);
        TempratureService statusService = new TempratureService(tempratureRepository);

        servletContext.addServlet("status", new StatusServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/status");

        servletContext.addServlet("record", new RecordTempratureServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/record");

        servletContext.addServlet("list_monthly_temp", new ListMonthlyTempDataServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/list_monthly_temp");

        servletContext.addServlet("list_yearly_temp", new ListYearlyTempDataServlet(trns.getTransactionRunner(), statusService))
                .addMapping("/list_yearly_temp");

        timer.schedule(new IndexMaintenanceJob(tempratureRepository, trns.getTransactionRunner()), 60 * 1000);
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
        dataSource.setConnectionProperties("sslmode=require");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(5);

        return new Transactions(dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
