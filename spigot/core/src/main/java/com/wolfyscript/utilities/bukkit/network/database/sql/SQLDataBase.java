/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.bukkit.network.database.sql;

import com.wolfyscript.utilities.WolfyUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDataBase {

    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    private final WolfyUtils api;

    private Connection connection;

    private final String dataBaseURL;
    private final String username;
    private final String password;
    private final String maxPool;
    private Properties properties;

    public SQLDataBase(WolfyUtils api, String host, String database, String username, String password, int port) {
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

    public WolfyUtils getApi() {
        return api;
    }
}
