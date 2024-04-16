package com.wolfyscript.utilities.bukkit.nms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties {

    private static Properties properties = null;

    public static Properties get() {
        if (properties == null) {
            properties = new Properties();
            try (BufferedReader is = new BufferedReader(new FileReader("server.properties"))) {
                properties.load(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

}
