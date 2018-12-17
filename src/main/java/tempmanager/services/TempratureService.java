package tempmanager.services;

import org.apache.log4j.Logger;
import tempmanager.models.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TempratureService {

    private static final Logger LOGGER = Logger.getLogger(TempratureService.class);

    private final Consumer<Runnable> writeTrns;

    private final Consumer<Runnable> readonlyTrns;

    private final TempratureRepository repository;

    private final int MY_HOME_ID = 1;

    public TempratureService(Consumer<Runnable> writeTrns, Consumer<Runnable> readonlyTrns, TempratureRepository repository) {
        this.writeTrns = writeTrns;
        this.readonlyTrns = readonlyTrns;
        this.repository = repository;
    }

    public StatusResult getTempratureStatus() {
        AtomicReference<StatusResult> result = new AtomicReference<>();
        readonlyTrns.accept(()-> {
            try {
                String timeZone = repository.getTimeZone();
                TempratureStatus tempratureStatus = repository.getTempratureStatus();
                List<TempratureHistory> tempWeekly = repository.listTempratureWeeklySummary();
                List<TempratureHistory> tempMonthly = repository.listTempratureMonthlySummary();

                result.set(new StatusResult(timeZone, tempWeekly, tempMonthly, tempratureStatus));
            } catch (RuntimeException ex) {
                LOGGER.error("execution failed.", ex);
                throw ex;
            }
        });
        return  result.get();
    }

    /**
     * Extract temprature from the following format.
     * /dev/hidraw1 0: temperature 30.00 °C
     * @param toolLog
     */
    public void recordTemprature(String toolLog) {
        writeTrns.accept(()-> {
            LOGGER.info("tool log=" + toolLog);
            try {
                String temp = toolLog.split(" ")[3];
                float temprature = Float.parseFloat(temp);
                repository.updateTemprature(temprature, MY_HOME_ID);
            } catch (RuntimeException ex) {
                LOGGER.error("failed!: " + toolLog, ex);
                throw ex;
            }
        });
    }

    public int getRecordTotal() {
        AtomicReference<Integer> result = new AtomicReference<>();
        writeTrns.accept(()-> {
            int count = repository.getRecordCount();
            result.set(count);
        });
        return result.get();
    }

    public List<TempratureStatus> listMonthlyTempDataSlow(int year, int month) {
        AtomicReference<List<TempratureStatus>> result = new AtomicReference<>();
        readonlyTrns.accept(()-> {
            List<TempratureStatus> tempratureStatuses = repository.listMonthlyTempDataSlow(year, month);
            result.set(tempratureStatuses);
        });

        return result.get();
    }

    public void insertLogStatus(String userAgent, String uri) {
        writeTrns.accept(()-> {
            repository.insertLogStatus(userAgent, uri);
        });
    }
}
