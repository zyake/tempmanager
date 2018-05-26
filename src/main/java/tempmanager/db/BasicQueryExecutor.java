package tempmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BasicQueryExecutor implements QueryExecutor {

    private static final Object[] NULL_OBJECTS = new Object[] {};

    private final Supplier<Connection> provider;

    private final Consumer<SQLException> sqlExceptionConsumer;


    public BasicQueryExecutor(Supplier<Connection> provider, Consumer<SQLException> sqlExceptionConsumer) {
        this.provider = provider;
        this.sqlExceptionConsumer = sqlExceptionConsumer;
    }

    @Override
    public void execute(String prepareStatement) {
        execute(prepareStatement, NULL_OBJECTS);
    }

    @Override
    public void execute(String prepareStatement, Object... params) {
        PreparedStatement statement = null;
        try {
            statement = provider.get().prepareStatement(prepareStatement);
            for (int i = 1; i <= params.length ; i ++) {
                statement.setObject(i, params[i - 1]);
            }
            statement.execute();
        } catch (SQLException e) {
            sqlExceptionConsumer.accept(e);
        }
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConsumer consumer, String prepareStatement) {
        return executeQuery(consumer, prepareStatement, NULL_OBJECTS);
    }

    @Override
    public <T> List<T> executeQuery(SQLThrowableConsumer consumer, String prepareStatement, Object... params) {
        List<T> results = new ArrayList<>();
        try {
            PreparedStatement statement = provider.get().prepareStatement(prepareStatement);
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
