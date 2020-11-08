package me.wolfyscript.utilities.api.utils.sql;

import me.wolfyscript.utilities.api.WolfyUtilities;

import java.sql.*;
import java.util.Properties;

public class SQLDataBase {

    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    private final WolfyUtilities api;

    private Connection connection;

    private final String dataBaseURL;
    private final String username;
    private final String password;
    private final String maxPool;
    private Properties properties;

    public SQLDataBase(WolfyUtilities api, String host, String database, String username, String password, int port) {
        this.api = api;
        this.username = username;
        this.password = password;
        this.maxPool = "250";
        this.dataBaseURL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf8";
    }

    /**
     * Creates the properties for the connection.
     */
    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("MaxPooledStatements", maxPool);
        }
        return properties;
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
     * Opens the connection on the main thread
     * This will cause some halt for the main thread, but is the safer method
     * to make sure the connection is established before executing a query!
     *
     * @see #openConnectionAsync() openConnectionAsync()
     */
    public Connection openConnection() {
        if (connection == null) {
            try {
                synchronized (this) {
                    Class.forName(DATABASE_DRIVER);
                    connection = DriverManager.getConnection(dataBaseURL, getProperties());
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Opens the connection to the database async on another thread!
     * Might cause some issues when a query is executed afterwards and the connection isn't established at that point.
     *
     * @see #openConnection()
     */
    public void openConnectionAsync() {
        new Thread(this::openConnection).start();
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void executeUpdate(PreparedStatement preparedStatement) {
        executeUpdate(preparedStatement, true);
    }

    public void executeUpdate(PreparedStatement preparedStatement, boolean async) {
        Runnable runnable = () -> {
            try {
                openConnection();
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
            } finally {
                closeConnection();
            }
        };
        if (async) {
            new Thread(runnable).start();
        } else {
            runnable.run();
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
     * It's recommended to use PreparedStatements instead of String to prevent security risks!
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

    /**
     * This method shouldn't be used on the main thread as it will block the thread till the data is received!
     * Call this method async using Bukkit.getScheduler().runTaskAsynchronously() or by creating an extra Thread manually!
     */
    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            openConnection();
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            api.sendConsoleWarning("Failed to execute SQL Query! " + e.getMessage());
            return null;
        } finally {
            closeConnection();
        }
    }

    public ResultSet getResultSet(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    public ResultSet getResultSet(String query) throws SQLException {
        return getResultSet(getPreparedStatement(query));
    }

    public WolfyUtilities getApi() {
        return api;
    }
}
