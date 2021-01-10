package me.wolfyscript.utilities.api.network.database.sql;

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
        this.dataBaseURL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&useUnicode=true&characterEncoding=utf8";
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
     * Opens the connection on the main thread. If the connection is already established this will just return it.
     * Attention! When using this on the main thread, it will cause it to hold till the connection is ready or times out!
     *
     * @return The established connection to the database
     */
    public Connection open() {
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

    public void close() {
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
        try {
            open(); //Makes sure that the connection is available. If not, it tries to connect.
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeAsyncUpdate(PreparedStatement preparedStatement) {
        new Thread(() -> executeUpdate(preparedStatement)).start();
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        try {
            open(); //Makes sure that the connection is available. If not, it tries to connect.
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public WolfyUtilities getApi() {
        return api;
    }
}
