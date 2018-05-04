package com.composite.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        DataSourceProperties properties = new DataSourceProperties();
        try {
            dataSource.setName(properties.getName());
            dataSource.setUrl(properties.getUrl());
            dataSource.setUsername(properties.getUsername());
            dataSource.setPassword(properties.getPassword());
            dataSource.setDriverClassName(properties.getDriverclassname());
            dataSource.setDbType(properties.getType());
            dataSource.setFilters(properties.getFilters());
            dataSource.setMaxActive(properties.getMaxActive());
            dataSource.setInitialSize(properties.getInitialSize());
            dataSource.setMaxWait(properties.getMaxWait());
            dataSource.setMinIdle(properties.getMinIdle());
            dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
            dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
            dataSource.setValidationQuery(properties.getValidationQuery());
            dataSource.setTestWhileIdle(properties.isTestWhileIdle());
            dataSource.setTestOnBorrow(properties.isTestOnBorrow());
            dataSource.setTestOnReturn(properties.isTestOnReturn());
            dataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
            dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        } catch (Exception e) {

        }
        return dataSource;
    }
}
