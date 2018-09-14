package tempmanager.servlets;

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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Properties;

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

        new ServletContextFactory(servletContext)
                .add("/status", new StatusServlet(trns.getTransactionRunner(), statusService))
                .add("/record", new RecordTempratureServlet(trns.getTransactionRunner(), statusService))
                .add("/list_monthly_temp_slow", new SlowListMonthlyTempDataServlet(trns.getTransactionRunner(), statusService));

        Properties serverConfig = new ServletFIleAccessor().readProperties(servletContext,"/WEB-INF/classes/mail.properties");
        jobManager
            .put(new IndexMaintenanceJob(tempratureRepository, trns.getTransactionRunner()))
            .put(new TempratureLimitCheckJob(trns.getTransactionRunner(), tempratureRepository, serverConfig));
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
