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


    /**
     * Old method to open the connection on the main thread!
     * use {@link #openConnection()} instead as it does the same thing!
     */
    @Deprecated
    public void openConnectionOnMainThread() {
        openConnection();
    }

    /**
     * Opens the connection to the database async on another thread!
     * Might cause some issues when a query is executed afterwards and the connection isn't established at that point.
     *
     * @see #openConnection()
     */
    public void openConnectionAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), this::openConnection);
    }

    /**
     * Opens the connection on the main thread
     * This will cause some halt for the main thread, but is the safer method
     * to make sure the connection is established before executing a query!
     *
     * @see #openConnectionAsync() openConnectionAsync()
     */
    public void openConnection() {
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

    public void executeUpdate(PreparedStatement preparedStatement) {
        executeUpdate(preparedStatement, true);
    }

    public void executeUpdate(PreparedStatement preparedStatement, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), () -> {
                try {
                    if (!connection.isValid(0)) {
                        openConnectionOnMainThread();
                    }
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
                }
            });
        } else {
            try {
                if (!connection.isValid(0)) {
                    openConnectionOnMainThread();
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
            }
        }
    }

    /**
     * This method executes the update on the main thread!
     * If the connection is lost it will also try to reconnect on the main thread causing it to halt!
     * It's recommended to only call it async!
     *
     * @param query The SQL query as a String
     **/
    public void executeUpdate(String query) {
        executeUpdate(query, false);
    }

    /**
     * Executes a String udate query. It creates a PreparedStatement from the String and executes it.
     * It's recommended to use PreparedStatements instead of String to prevent secrurity risks!
     *
     * @param query The SQL query as a String
     * @param async If the update should be executed on a different thread
     **/
    @Deprecated
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

    /*
    This method shouldn't be used on the main thread as it will cause it to halt till the data is recieved!
    Call this method async using Bukkit.getScheduler().runTaskAsynchronously() or by creating an extra Thread manually!
     */
    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            if (!connection.isValid(0)) {
                openConnectionOnMainThread();
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
            return null;
        }
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
