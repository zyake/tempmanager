package tempmanager.servlets;

import org.apache.commons.dbcp.BasicDataSource;
import tempmanager.db.BasicQueryExecutorFactory;
import tempmanager.db.QueryExecutor;
import tempmanager.db.ServletTransactionsFactory;
import tempmanager.db.Transactions;
import tempmanager.io.ServletFIleAccessor;
import tempmanager.jobs.IndexMaintenanceJob;
import tempmanager.jobs.JobManager;
import tempmanager.jobs.TempratureLimitCheckJob;
import tempmanager.models.TempratureRepository;
import tempmanager.services.TempratureService;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class InitListener implements ServletContextListener {

    private final JobManager jobManager = new JobManager(60, 1);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        Transactions trns = new ServletTransactionsFactory().createTransactions(servletContext);
        String sqlDirPath = servletContext.getRealPath("/WEB-INF/sqls");
        QueryExecutor queryExecutor = trns.getQueryExecutor(sqlDirPath, BasicQueryExecutorFactory.DEFAULT_EXCEPTION_HANDLER);

        TempratureRepository tempratureRepository = new TempratureRepository(queryExecutor);
        TempratureService statusService = new TempratureService(tempratureRepository);

        Properties serverConfig = ServletFIleAccessor.readProperties(servletContext,"/WEB-INF/classes/mail.properties");

        new ServletContextFactory(servletContext)
                .addServlet("/status", new StatusServlet(trns.getTransactionRunner(), statusService))
                .addServlet("/record", new RecordTempratureServlet(trns.getTransactionRunner(), statusService))
                .addServlet("/list_monthly_temp", new ListMonthlyTempDataServlet(trns.getTransactionRunner(), statusService))
                .addServlet("/list_yearly_temp", new ListYearlyTempDataServlet(trns.getTransactionRunner(), statusService))
                .addServlet("/list_monthly_temp_slow", new SlowListMonthlyTempDataServlet(trns.getTransactionRunner(), statusService));

        jobManager
            .scheduleJob(new IndexMaintenanceJob(tempratureRepository, trns.getTransactionRunner()))
            .scheduleJob(new TempratureLimitCheckJob(trns.getTransactionRunner(), tempratureRepository, serverConfig));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            jobManager.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
