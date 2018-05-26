package tempmanager.models;

import tempmanager.db.QueryExecutor;

import java.util.List;

public class TempratureRepository {

    private final QueryExecutor executor;

    public TempratureRepository(QueryExecutor executor) {
        this.executor = executor;
    }

    public TempratureStatus getTempratureStatus() {
        List<TempratureStatus> results = executor.executeQuery(
                rs -> new TempratureStatus(rs.getFloat(1), rs.getString(2)),
                "getTempratureStatus");
        return results.get(0);
    }

    public List<TempratureHistory> listTempratureHitories() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                "listTempratureHistories");
    }

    public String getTimeZone() {
        List<String> results = executor.executeQuery((rs) -> rs.getString(1), "getTimezone");
        return results.get(0);
    }

    public void recordTemprature(float temp, int locationId) {
        executor.execute("recordTemprature", temp, locationId);
    }

    public int getRecordCount() {
        List<Integer> intValues = executor.executeQuery((rs) -> rs.getInt(1), "getRecordCount");
        return intValues.get(0);
    }

    public void refreshTempratureSummary() {
        executor.execute("refreshTempratureSummary");
    }

    public void refreshTempratureTotalCount() {
        executor.execute("refreshTotalCount");
    }

}
