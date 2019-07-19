package me.wolfyscript.utilities.api.config;


import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class SQLDataBase {

    private WolfyUtilities api;

    private Connection connection;

    private String host, database, username, password;
    private int port;

    public SQLDataBase(WolfyUtilities api, String host, String database, String username, String password, int port) {
        this.api = api;

        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;

        openConnection();

    }

    public void openConnection(){
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        return;
                    }
                    synchronized (this) {
                        if (connection != null && !connection.isClosed()) {
                            return;
                        }

                        Class.forName("com.mysql.jdbc.Driver");

                        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.runTaskAsynchronously(api.getPlugin());
    }

    public void executeUpdate(PreparedStatement preparedStatement) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(api.getPlugin());
    }

    public void executeUpdate(String query) {
        try {
            executeUpdate(connection.prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public ResultSet getResultSet(String query) throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement(query);
        return preparedStatement.executeQuery();
    }

}
