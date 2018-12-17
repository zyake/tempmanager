package tempmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface QueryExecutor {

    void execute(QueryKey key);

    void execute(QueryKey key, Object... params);

    <T> List<T> executeQuery(SQLThrowableConverter<ResultSet> consumer, QueryKey key);

    <T> List<T> executeQuery(SQLThrowableConverter<ResultSet> consumer, QueryKey key, Object... params);

    void executeAsStream(SQLThrowableConsumer<ResultSet> consumer, QueryKey key);

    void executeAsStream(SQLThrowableConsumer<ResultSet> consumer, QueryKey key, Object... params);

    void setThreadLocalStatementModifier(SQLThrowableConsumer<PreparedStatement> statement);

    Map<String, String> getSqlMap();

    boolean isReadOnly();
}
