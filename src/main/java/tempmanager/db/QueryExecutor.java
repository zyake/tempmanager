package tempmanager.db;

import java.util.List;

public interface QueryExecutor {

    void execute(QueryKey key);

    void execute(QueryKey key, Object... params);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, QueryKey key);

    <T> List<T> executeQuery(SQLThrowableConsumer consumer, QueryKey key, Object... params);
}
