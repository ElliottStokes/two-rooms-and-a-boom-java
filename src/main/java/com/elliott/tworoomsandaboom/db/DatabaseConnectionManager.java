package com.elliott.tworoomsandaboom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private final String url;
    private final Properties properties;

    public DatabaseConnectionManager(String host, String port, String databaseName, String username) {
        this.url = "jdbc:mysql://"+host+":"+port+"/"+databaseName;
        this.properties = new Properties();
        this.properties.setProperty("user", username);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.properties);
    }
}
