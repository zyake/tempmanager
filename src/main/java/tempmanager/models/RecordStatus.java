package tempmanager.models;

public class RecordStatus {

    private final int recordCount;

    private final int todayRecordCount;

    public RecordStatus(int recordCount, int todayRecordCount) {
        this.recordCount = recordCount;
        this.todayRecordCount = todayRecordCount;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public int getTodayRecordCount() {
        return todayRecordCount;
    }
}
