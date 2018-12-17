package tempmanager.db;

import java.util.Collections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BasicQueryExecutorFactory {

    public static final BiConsumer<String, SQLException> DEFAULT_EXCEPTION_HANDLER = (sql, exp) -> {
        throw new RuntimeException(String.format("SQL exescution failed!: SQL=%s, SQL state=%s", sql, exp.getSQLState()), exp);
    };

    public Supplier<Connection> createTransactionMappingAccessor(Transactions... trns) {
        Map<Transactions, Supplier<Connection>> hashMap = new HashMap<>();
        for (Transactions trn : trns) {
            hashMap.put(trn, trn.getConnectionAccessor());
        }
        return ()-> {
          Transactions activeTrn = Transactions.getCurrentActiveTransaction();
          return hashMap.get(activeTrn).get();
        };
    }

    public QueryExecutor newInstance(String rootDir, Supplier<Connection> connectionAccesser, BiConsumer<String, SQLException> exceptionHandler) {
        Map<String, String> sqlMap = new HashMap<>();
        File file = new File(rootDir);
        File[] files = file.listFiles();
        if (files != null) {
            for (File sql : files) {
                if (sql.isDirectory()) {
                    continue;
                }
                if (!sql.getName().endsWith(".sql")) {
                    continue;
                }

                try (FileInputStream inputStream = new FileInputStream(sql)) {
                    byte[] dataBytes = new byte[(int) sql.length()];
                    inputStream.read(dataBytes);
                    String sqlText = new String(dataBytes, "UTF-8");
                    String sqlCommand = sql.getName().substring(0, sql.getName().length() - 4);
                    sqlMap.put(sqlCommand, sqlText);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Map<String, String> readOnlySqlMap = Collections.unmodifiableMap(sqlMap);

        return new BasicQueryExecutor(readOnlySqlMap, connectionAccesser, exceptionHandler);
    }
}
