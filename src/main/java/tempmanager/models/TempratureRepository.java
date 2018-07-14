package tempmanager.models;

import tempmanager.db.QueryExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class TempratureRepository {

    private final QueryExecutor executor;

    public TempratureRepository(QueryExecutor executor) {
        this.executor = executor;
    }

    public TempratureStatus getTempratureStatus() {
        List<TempratureStatus> results = executor.executeQuery(
                rs -> new TempratureStatus(rs.getFloat(1), rs.getString(2)),
                QueryKeys.getTempratureStatus);
        return results.get(0);
    }

    public List<TempratureHistory> listTempratureHitories() {
        return executor.executeQuery((rs) -> new TempratureHistory(
                rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                QueryKeys.listTempratureHistories);
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

    public void listMonthlyTempData(int year, int month, OutputStream outputStream) {
        String date = year + "-" + month + "-01";
        listTempDataInternal(outputStream, date, date);
    }

    public void listYearlyTempData(int year, OutputStream outputStream) {
        String startDate = year + "-01-01";
        String endDate = year + "-12-31";
        listTempDataInternal(outputStream, startDate, endDate);
    }

    public void maintenanceIndexes() {
        executor.execute(QueryKeys.maintenanceIndexes);
    }

    protected void listTempDataInternal(OutputStream outputStream, String date, String date2) {
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            writer.write("timestamp,temprature\r\n");
            executor.setThreadLocalStatementModifier((ps) -> {
                ps.setFetchSize(10);
            });
            executor.executeAsStream((rs) -> {
                        try {
                            writer.write("\"" + rs.getObject(1) + "\"");
                            writer.write(",");
                            writer.write(Float.toString(rs.getFloat(2)));
                            writer.write("\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    QueryKeys.listTempratureRecords, date, date2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
