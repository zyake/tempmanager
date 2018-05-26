package tempmanager.models;

public class TempratureStatus {

    private final float temprature;

    private final String recordedTimestamp;

    public TempratureStatus(float temprature, String recordedTimestamp) {
        this.temprature = temprature;
        this.recordedTimestamp = recordedTimestamp;
    }

    public float getTemprature() {
        return temprature;
    }

    public String getRecordedTimestamp() {
        return recordedTimestamp;
    }
}
