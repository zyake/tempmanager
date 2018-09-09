package tempmanager.services;

import org.apache.log4j.Logger;
import tempmanager.models.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class TempratureService {

    private static final Logger LOGGER = Logger.getLogger(TempratureService.class);

    private final TempratureRepository repository;

    private final int MY_HOME_ID = 1;

    public TempratureService(TempratureRepository repository) {
        this.repository = repository;
    }

    public StatusResult TempratureStatus() {
        try {
            String timeZone = repository.getTimeZone();
            TempratureStatus tempratureStatus = repository.getTempratureStatus();
            List<TempratureHistory> tempWeekly = repository.listTempratureWeeklySummary();
            List<TempratureHistory> tempMonthly = repository.listTempratureMonthlySummary();

            return new StatusResult(timeZone, tempWeekly, tempMonthly, tempratureStatus);
        } catch (RuntimeException ex) {
            LOGGER.error("execution failed.", ex);
            throw ex;
        }
    }

    /**
     * Extract temprature from the following format.
     * /dev/hidraw1 0: temperature 30.00 °C
     * @param toolLog
     */
    public void recordTemprature(String toolLog) {
        LOGGER.info("tool log=" + toolLog);
        try {
            String temp = toolLog.split(" ")[3];
            float temprature = Float.parseFloat(temp);
            repository.updateTemprature(temprature, MY_HOME_ID);
        } catch (RuntimeException ex) {
            LOGGER.error("failed!: " + toolLog, ex);
            throw ex;
        }
    }

    public int getRecordTotal() {
        return repository.getRecordCount();
    }

    public void listMonthlyTempData(int year, int month, Writer writer) {
      repository.listMonthlyTempData(year, month, writer);
    }

    public List<TempratureStatus> listMonthlyTempDataSlow(int year, int month) {
        return repository.listMonthlyTempDataSlow(year, month);
    }

    public void listYearlyTempData(int year, Writer writer) {
        repository.listYearlyTempData(year, writer);
    }

    public void insertLogStatus(String userAgent, String uri) {
        repository.insertLogStatus(userAgent, uri);
    }
}
