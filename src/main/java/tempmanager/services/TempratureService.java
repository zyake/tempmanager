package tempmanager.services;

import org.apache.log4j.Logger;
import tempmanager.models.*;

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
            List<TempratureHistory> tempratureHistories = repository.listTempratureHitories();

            LOGGER.debug("request completed: " + tempratureStatus.getTemprature() + ", " + tempratureStatus.getRecordedTimestamp());

            return new StatusResult(timeZone, tempratureHistories, tempratureStatus);
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
            repository.recordTemprature(temprature, MY_HOME_ID);
            repository.refreshTempratureSummary();
            repository.refreshTempratureTotalCount();
        } catch (RuntimeException ex) {
            LOGGER.error("failed!: " + toolLog, ex);
            throw ex;
        }
    }

    public int getRecordTotal() {
        return repository.getRecordCount();
    }
}
