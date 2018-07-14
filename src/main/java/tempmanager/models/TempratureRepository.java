package tempmanager.models;

import tempmanager.db.QueryExecutor;
import tempmanager.db.QueryKey;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        Connection connection = executor.getConnection();
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            writer.write("timestamp,temprature\r\n");
            PreparedStatement statement  = connection.prepareStatement(executor.getSqlMap().get(QueryKeys.listTempratureRecords.toString()));
            statement.setObject(1, date);
            statement.setObject(2, date);
            statement.setFetchSize(10);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                writer.write( "\"" + resultSet.getObject(1) + "\"");
                writer.write(",");
                writer.write(Float.toString(resultSet.getFloat(2)));
                writer.write("\r\n");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void listYearlyTempData(int year, OutputStream outputStream) {
        String startDate = year + "-01-01";
        String endDate = year + "-12-31";
        Connection connection = executor.getConnection();
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            writer.write("timestamp,temprature\r\n");
            PreparedStatement statement  = connection.prepareStatement(executor.getSqlMap().get(QueryKeys.listTempratureRecords.toString()));
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            statement.setFetchSize(10);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                writer.write( "\"" + resultSet.getObject(1) + "\"");
                writer.write(",");
                writer.write(Float.toString(resultSet.getFloat(2)));
                writer.write("\r\n");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
