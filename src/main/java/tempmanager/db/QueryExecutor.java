package tempmanager.db;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface QueryExecutor {

    void execute(QueryKey key);

    void execute(QueryKey key, Object... params);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, QueryKey key);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, QueryKey key, Object... params);

    Map<String, String> getSqlMap();

    Connection getConnection();
}
