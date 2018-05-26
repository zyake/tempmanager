package tempmanager.models;

public class TempratureHistory {

    private final String minTemp;

    private final String maxTemp;

    private final String avgTemp;

    private final String date;

    public TempratureHistory(String minTemp, String maxTemp, String avgTemp, String date) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.avgTemp = avgTemp;
        this.date = date;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getAvgTemp() {
        return avgTemp;
    }

    public String getDate() {
        return date;
    }
}
