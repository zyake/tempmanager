package tempmanager.db;

import java.util.List;

public interface QueryExecutor {

    void execute(String key);

    void execute(String prepareStatement, Object... params);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, String key, Object... params);
}
