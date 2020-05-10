package me.wolfyscript.utilities.api.utils.sql;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;

import java.sql.*;

public class SQLDataBase {

    private final WolfyUtilities api;

    private Connection connection;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

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
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf8", username, password);
                api.sendConsoleMessage("Connected to MySQL DataBase");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void openConnectionAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), this::openConnectionOnMainThread);
    }

    public void openConnection() {
        openConnectionAsync();
    }

    public void executeUpdate(PreparedStatement preparedStatement) {
        executeUpdate(preparedStatement, true);
    }

    public void executeUpdate(PreparedStatement preparedStatement, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), () -> {
                try {
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
                }
            });
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
