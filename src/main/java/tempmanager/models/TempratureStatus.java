package tempmanager.models;

public class TempratureStatus {

    private final String temprature;

    private final String recordedTimestamp;

    private final int tempLimit;

    public TempratureStatus(float temprature, String recordedTimestamp, int tempLimit) {
        this.temprature = Integer.toString(Math.round(temprature));
        this.recordedTimestamp = recordedTimestamp;
        this.tempLimit = tempLimit;
    }

    public String getTemprature() {
        return temprature;
    }

    public String getRecordedTimestamp() {
        return recordedTimestamp;
    }

    public int getTempLimit() {
        return tempLimit;
    }
}
