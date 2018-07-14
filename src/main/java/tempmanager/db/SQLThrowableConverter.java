package tempmanager.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLThrowableConverter<T> {
    Object consume(T rs) throws SQLException;
}
