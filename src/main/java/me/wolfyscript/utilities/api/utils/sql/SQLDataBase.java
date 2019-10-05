package me.wolfyscript.utilities.api.utils.sql;

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
    }

    public void openConnectionOnMainThread() {
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
                api.sendConsoleMessage("Connected to MySQL DataBase");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                openConnectionOnMainThread();
            }
        };
        runnable.runTaskAsynchronously(api.getPlugin());
    }

    public void executeUpdate(PreparedStatement preparedStatement) {
        executeUpdate(preparedStatement, true);
    }

    public void executeUpdate(PreparedStatement preparedStatement, boolean async) {
        if(async){
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
        }else{
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
            }
        }
    }

    public void executeUpdate(String query) {
        executeUpdate(query, false);
    }

    public void executeUpdate(String query, boolean async) {
        try {
            executeUpdate(connection.prepareStatement(query), async);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public ResultSet getResultSet(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    public ResultSet getResultSet(String query) throws SQLException {
        return getResultSet(getPreparedStatement(query));
    }

    public Connection getConnection() {
        return connection;
    }

    public WolfyUtilities getApi() {
        return api;
    }
}
