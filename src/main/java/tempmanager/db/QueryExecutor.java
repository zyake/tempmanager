package tempmanager.db;

import java.util.List;
import java.util.Map;

public interface QueryExecutor {

    Map<String, String> getSqlMap();

    void execute(String key);

    void execute(String key, Object... params);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key, Object... params);
}
