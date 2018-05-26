package tempmanager.models;

import java.util.List;

public class StatusResult {

    private final String timezone;

    private final List<TempratureHistory> histories;

    private final TempratureStatus status;

    public StatusResult(String timezone, List<TempratureHistory> histories, TempratureStatus status) {
        this.timezone = timezone;
        this.histories = histories;
        this.status = status;
    }

    public String GetTimezone() {
        return timezone;
    }

    public List<TempratureHistory> getHisoties() {
        return histories;
    }

    public TempratureStatus getStatus() {
        return status;
    }
}
