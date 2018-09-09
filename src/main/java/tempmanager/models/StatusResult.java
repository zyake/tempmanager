package tempmanager.models;

import java.util.List;

public class StatusResult {

    private final String timezone;

    private final List<TempratureHistory> tempWeekly;

    private final List<TempratureHistory> tempMonthly;

    private final TempratureStatus status;

    public StatusResult(String timezone, List<TempratureHistory> tempWeekly, List<TempratureHistory> tempMonthly, TempratureStatus status) {
        this.timezone = timezone;
        this.tempWeekly = tempWeekly;
        this.tempMonthly = tempMonthly;
        this.status = status;
    }

    public String GetTimezone() {
        return timezone;
    }

    public List<TempratureHistory> getTempWeekly() {
        return tempWeekly;
    }

    public List<TempratureHistory> getTempMonthly() {
        return tempMonthly;
    }

    public TempratureStatus getStatus() {
        return status;
    }
}
