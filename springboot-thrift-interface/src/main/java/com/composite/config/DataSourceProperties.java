package com.composite.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceProperties {
    private String name;

    private String url;

    private String username;

    private String password;

    private String driverclassname;

    private String type;

    private String filters;

    private int maxActive;

    private int initialSize;

    private int maxWait;

    private int minIdle;

    private int timeBetweenEvictionRunsMillis;

    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxOpenPreparedStatements;


    public DataSourceProperties() {
        InputStream inputStream = this.getClass().getResourceAsStream("/application.properties");
        Properties config = new Properties();
        try {
            config.load(inputStream);
            this.name = config.getProperty("spring.datasource.name");
            this.url = config.getProperty("spring.datasource.url");
            this.username = config.getProperty("spring.datasource.username");
            this.password = config.getProperty("spring.datasource.password");
            this.driverclassname = config.getProperty("spring.datasource.driver-class-name");
            this.type = config.getProperty("spring.datasource.type");
            this.filters = config.getProperty("spring.datasource.filters");
            this.maxActive = Integer.parseInt(config.getProperty("spring.datasource.maxActive"));
            this.initialSize = Integer.parseInt(config.getProperty("spring.datasource.initialSize"));
            this.maxWait = Integer.parseInt(config.getProperty("spring.datasource.maxWait"));
            this.minIdle = Integer.parseInt(config.getProperty("spring.datasource.minIdle"));
            this.timeBetweenEvictionRunsMillis = Integer.parseInt(config.getProperty("spring.datasource.timeBetweenEvictionRunsMillis"));
            this.minEvictableIdleTimeMillis = Integer.parseInt(config.getProperty("spring.datasource.minEvictableIdleTimeMillis"));
            this.validationQuery = config.getProperty("spring.datasource.validationQuery");
            this.testWhileIdle = Boolean.parseBoolean(config.getProperty("spring.datasource.testWhileIdle"));
            this.testOnBorrow = Boolean.parseBoolean(config.getProperty("spring.datasource.testOnBorrow"));
            this.testOnReturn = Boolean.parseBoolean(config.getProperty("spring.datasource.testOnReturn"));
            this.poolPreparedStatements = Boolean.parseBoolean(config.getProperty("spring.datasource.poolPreparedStatements"));
            this.maxOpenPreparedStatements = Integer.parseInt(config.getProperty("spring.datasource.maxOpenPreparedStatements"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverclassname() {
        return driverclassname;
    }

    public String getType() {
        return type;
    }

    public String getFilters() {
        return filters;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }
}
