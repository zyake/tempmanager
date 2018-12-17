package tempmanager.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Transactions {

    private final static ThreadLocal<Transactions> currentActiveTransaction = new InheritableThreadLocal<>();

    private final ThreadLocal<Connection> currentConnection = new InheritableThreadLocal<>();

    private final DataSource dataSource;

    private final Consumer<Exception> exceptionHandler;

    private final Consumer<Connection> connectionInitializer;

    public Transactions(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionHandler = (ex) -> { throw new RuntimeException(ex); };
        this.connectionInitializer = (c)-> {};
    }

    public Transactions(DataSource dataSource, Consumer<Exception> exceptionHandler, Consumer<Connection> connectionInitializer) {
        this.dataSource = dataSource;
        this.exceptionHandler = exceptionHandler;
        this.connectionInitializer = connectionInitializer;
    }

    protected void runTrn(Runnable trn) {
        currentActiveTransaction.set(this);
        Connection connection = null;
        try {
            if (currentConnection.get() != null) {
                throw new RuntimeException("Duplicated transaction!: " + currentConnection.get());
            }
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connectionInitializer.accept(connection);
            currentConnection.set(connection);
            trn.run();
            connection.commit();
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            exceptionHandler.accept(e);
        } finally {
            currentActiveTransaction.set(null);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            currentConnection.set(null);
        }
    }

    public Supplier<Connection> getConnectionAccessor() {
        return () ->  {
            Connection conn = currentConnection.get();
            if (conn == null) {
                throw new RuntimeException("Transaction has not been started!");
            }
            return conn;
        };
    }

    public Consumer<Runnable> getTransactionRunner() {
        return (trn)-> runTrn(trn);
    }

    public static Transactions getCurrentActiveTransaction() {
        return currentActiveTransaction.get();
    }
}
