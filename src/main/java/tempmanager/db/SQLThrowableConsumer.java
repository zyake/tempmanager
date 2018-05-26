package tempmanager.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLThrowableConsumer {

    Object consume(ResultSet rs) throws SQLException;
}
