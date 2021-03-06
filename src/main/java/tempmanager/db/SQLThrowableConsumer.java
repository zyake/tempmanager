package tempmanager.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLThrowableConsumer<T> {
    void accept(T rs) throws SQLException;
}
