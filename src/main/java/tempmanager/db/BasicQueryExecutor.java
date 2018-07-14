package tempmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BasicQueryExecutor implements QueryExecutor {

    private static final Object[] NULL_OBJECTS = new Object[] {};

    private final Map<String, String> sqlMap;

    private final Supplier<Connection> provider;

    private final BiConsumer<String, SQLException> sqlExceptionConsumer;

    private final ThreadLocal<SQLThrowableConsumer<PreparedStatement>> threadLocalStatementModifier = new InheritableThreadLocal<>();

    public BasicQueryExecutor(Map<String, String> sqlMap, Supplier<Connection> provider, BiConsumer<String, SQLException> sqlExceptionConsumer) {
        this.sqlMap = sqlMap;
        this.provider = provider;
        this.sqlExceptionConsumer = sqlExceptionConsumer;
    }

    @Override
    public void execute(QueryKey key) {
        execute(key, NULL_OBJECTS);
    }

    @Override
    public void execute(QueryKey key, Object... params) {
        PreparedStatement statement = null;
        String sqlText = null;
        try {
            sqlText = sqlMap.get(key.toString());
            if (sqlText == null) {
                throw new RuntimeException("SQL not found!: KEY=" + key + ", sql map=" + sqlMap);
            }

            statement = provider.get().prepareStatement(sqlText);
            SQLThrowableConsumer<PreparedStatement> modifier = threadLocalStatementModifier.get();
            if (modifier == null) {
                modifier.accept(statement);
            }

            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            statement.execute();
        } catch (SQLException e) {
            sqlExceptionConsumer.accept(sqlText, e);
        }
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConverter<ResultSet> consumer, QueryKey key) {
        return executeQuery(consumer, key, NULL_OBJECTS);
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConverter<ResultSet> consumer, QueryKey key, Object... params) {
        List<T> results = new ArrayList<>();
        String sqlText = null;
        try {
            sqlText = sqlMap.get(key.toString());
            if (sqlText == null) {
                throw new RuntimeException("SQL not found!: KEY=" + key  + ", sql map=" + sqlMap);
            }
            PreparedStatement statement = provider.get().prepareStatement(sqlText);
            SQLThrowableConsumer<PreparedStatement> modifier = threadLocalStatementModifier.get();
            if (modifier != null) {
                modifier.accept(statement);
            }
            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T result = (T) consumer.consume(resultSet);
                results.add(result);
            }
        } catch (SQLException e) {
             sqlExceptionConsumer.accept(sqlText, e);
        }
        return results;
    }

    @Override
    public void executeAsStream(SQLThrowableConsumer<ResultSet> consumer, QueryKey key) {
        executeAsStream(consumer, key, NULL_OBJECTS);
    }

    @Override
    public void executeAsStream(SQLThrowableConsumer<ResultSet> consumer, QueryKey key, Object... params) {
        String sqlText = null;
        try {
            sqlText = sqlMap.get(key.toString());
            if (sqlText == null) {
                throw new RuntimeException("SQL not found!: KEY=" + key  + ", sql map=" + sqlMap);
            }
            PreparedStatement statement = provider.get().prepareStatement(sqlText);
            SQLThrowableConsumer<PreparedStatement> modifier = threadLocalStatementModifier.get();
            if (modifier != null) {
                modifier.accept(statement);
            }
            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        } catch (SQLException e) {
            sqlExceptionConsumer.accept(sqlText, e);
        }
    }

    @Override
    public void setThreadLocalStatementModifier(SQLThrowableConsumer<PreparedStatement> statement) {
        threadLocalStatementModifier.set(statement);
    }

    public Map<String, String> getSqlMap() {
        return this.sqlMap;
    }
}
