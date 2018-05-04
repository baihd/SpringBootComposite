package com.composite.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    private String zk_connectString;
    private int zk_sessionTimeOut;
    private int zk_connectionTimeout;

    public ConfigUtils() {
        InputStream inputStream = this.getClass().getResourceAsStream("/zookeeper.properties");
        Properties config = new Properties();
        try {
            config.load(inputStream);
            this.zk_connectString = config.getProperty("zk.connectString");
            this.zk_sessionTimeOut = Integer.parseInt(config.getProperty("zk.sessionTimeout"));
            this.zk_connectionTimeout = Integer.parseInt(config.getProperty("zk.connectionTimeout"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getZk_connectString() {
        return zk_connectString;
    }

    public int getZk_sessionTimeOut() {
        return zk_sessionTimeOut;
    }

    public int getZk_connectionTimeout() {
        return zk_connectionTimeout;
    }
}
