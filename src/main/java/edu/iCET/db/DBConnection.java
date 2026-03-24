package edu.iCET.db;

import edu.iCET.util.EnvLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private final Connection connection;

    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection(
                EnvLoader.get("DB_URL"),
                EnvLoader.get("DB_USER"),
                EnvLoader.get("DB_PASSWORD")
        );
    }

    public Connection getConnection() {
        return connection;
    }

    public static DBConnection getInstance() throws SQLException {
        return null == instance ? instance = new DBConnection() : instance;
    }
}