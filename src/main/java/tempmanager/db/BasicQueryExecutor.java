package tempmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BasicQueryExecutor implements QueryExecutor {

    private static final Object[] NULL_OBJECTS = new Object[] {};

    private final Map<String, String> sqlMap;

    private final Supplier<Connection> provider;

    private final Consumer<SQLException> sqlExceptionConsumer;

    public BasicQueryExecutor(Map<String, String> sqlMap, Supplier<Connection> provider, Consumer<SQLException> sqlExceptionConsumer) {
        this.sqlMap = sqlMap;
        this.provider = provider;
        this.sqlExceptionConsumer = sqlExceptionConsumer;
    }

    @Override
    public Map<String, String> getSqlMap() {
        return sqlMap;
    }

    @Override
    public void execute(String key) {
        execute(key, NULL_OBJECTS);
    }

    @Override
    public void execute(String key, Object... params) {
        PreparedStatement statement = null;
        try {
            String sqlText = sqlMap.get(key);
            if (sqlText == null) {
                throw new RuntimeException("SQL not found!: KEY=" + key + ", sql map=" + sqlMap);
            }

            statement = provider.get().prepareStatement(sqlText);
            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            statement.execute();
        } catch (SQLException e) {
            sqlExceptionConsumer.accept(e);
        }
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key) {
        return executeQuery(consumer, key, NULL_OBJECTS);
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key, Object... params) {
        List<T> results = new ArrayList<>();
        try {
            String sqlText = sqlMap.get(key);
            if (sqlText == null) {
                throw new RuntimeException("SQL not found!: KEY=" + key  + ", sql map=" + sqlMap);
            }
            PreparedStatement statement = provider.get().prepareStatement(sqlText);
            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T result = (T) consumer.consume(resultSet);
                results.add(result);
            }
        } catch (SQLException e) {
             sqlExceptionConsumer.accept(e);
        }
        return results;
    }
}
