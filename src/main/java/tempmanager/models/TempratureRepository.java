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
                "SELECT temprature, recorded_timestamp FROM temprature_records " +
                        "WHERE id = ( SELECT MAX(id) FROM temprature_records )");
        return results.get(0);
    }

    public List<TempratureHistory> listTempratureHitories() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)),
                "SELECT MAX(temprature), MIN(temprature), AVG(temprature), timestamp_date  " +
                        "FROM temprature_records " +
                        "GROUP BY timestamp_date " +
                        "ORDER BY timestamp_date DESC");
    }

    public String getTimeZone() {
        List<String> results = executor.executeQuery((rs) -> rs.getString(1), "SHOW timezone");

        return results.get(0);
    }

    public void recordTemprature(float temp, int locationId) {
        executor.execute("INSERT INTO temprature_records(id, recorded_timestamp, timestamp_date, temprature, locations_id) " +
                "VALUES(NEXTVAL('temprature_records_seq'), current_timestamp,current_timestamp::date, ?, ?)", temp, locationId);
    }

    public int getRecordCount() {
        List<Integer> intValues = executor.executeQuery((rs) -> rs.getInt(1), "SELECT COUNT(*) AS COUNT FROM temprature_records");
        return intValues.get(0);
    }

    public int countTodayRecords() {
        List<Integer> intValues = executor.executeQuery((rs) -> rs.getInt(1), "SELECT COUNT(*) AS COUNT FROM temprature_records " +
                "WHERE recorded_timestamp BETWEEN  current_date::timestamp AND (current_date || ' 23:59:59+09')::timestamp");
        return intValues.get(0);
    }
}
