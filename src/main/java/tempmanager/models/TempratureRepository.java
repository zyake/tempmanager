package tempmanager.models;

import tempmanager.db.QueryExecutor;

import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TempratureRepository {

    private final QueryExecutor executor;

    public TempratureRepository(QueryExecutor executor) {
        this.executor = executor;
    }

    public TempratureStatus getTempratureStatus() {
        List<TempratureStatus> results = executor.executeQuery(
                rs -> new TempratureStatus(rs.getFloat(1), rs.getString(2), rs.getInt(3)),
                QueryKeys.getTempratureStatus);
        return results.get(0);
    }

    public List<TempratureHistory> listTempratureHitories() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                QueryKeys.listTempratureHistories);
    }

    public List<TempratureHistory> listTempratureWeeklySummary() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                QueryKeys.listTempratureWeeklySummary);
    }

    public List<TempratureHistory> listTempratureMonthlySummary() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                QueryKeys.listTempratureMonthlySummary);
    }

    public String getTimeZone() {
        List<String> results = executor.executeQuery((rs) -> rs.getString(1), QueryKeys.getTimezone);
        return results.get(0);
    }

    public int getRecordCount() {
        List<Integer> intValues = executor.executeQuery((rs) -> rs.getInt(1), QueryKeys.getRecordCount);
        return intValues.get(0);
    }

    public void updateTemprature(float temp, int locationId) {
        executor.execute(QueryKeys.updateTemprature, temp, locationId);
    }

    public void listMonthlyTempData(int year, int month, Writer writer) {
        String date1 = year + "-" + month + "-01";
        String date2 = year + "-" + month + "-30";
        listTempDataInternal(writer, date1, date2);
    }

    public void listYearlyTempData(int year, Writer writer) {
        String startDate = year + "-01-01";
        String endDate = year + "-12-31";
        listTempDataInternal(writer, startDate, endDate);
    }

    public void maintenanceIndexes() {
        executor.execute(QueryKeys.maintenanceIndexes);
    }

    protected void listTempDataInternal(Writer writer, String date, String date2) {
        try {
            writer.write("timestamp,temprature\r\n");
            executor.invoke((conn) -> {
                conn.setAutoCommit(false);
                PreparedStatement statement = conn.prepareStatement("SELECT recorded_timestamp, temprature + (SELECT adjust FROM  temprature_adjust WHERE id = 1) FROM temprature_records WHERE timestamp_date >= ?::date AND timestamp_date <= ?::date");
                statement.setObject(1, date);
                statement.setObject(2, date2);

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    try {
                        writer.write(rs.getObject(1).toString());
                        writer.write(",");
                        writer.write(Integer.toString(Math.round(rs.getFloat(2))));
                        writer.write("\r\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertLogStatus(String userAgent, String uri) {
        executor.execute(QueryKeys.insertLogStatus, userAgent, uri);
    }

    public List<TempratureStatus> listMonthlyTempDataSlow(int year, int month) {
        String startDate = year + "-" + month + "-01";
        String endDate = year + "-" + month + "-30";
        return executor.executeQuery(
                (rs) -> new TempratureStatus(rs.getFloat(1), rs.getString(2), rs.getInt(3)),
                QueryKeys.listTempratureRecords, startDate, endDate);
    }

    public String getPassword(String key) {
        return (String) executor.executeQuery((rs) -> rs.getString(1),
                QueryKeys.getPassword, key).get(0);
    }
}
