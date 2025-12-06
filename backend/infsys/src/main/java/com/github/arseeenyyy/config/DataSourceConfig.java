package com.github.arseeenyyy.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DataSourceConfig {
    private static final Logger logger = Logger.getLogger(DataSourceConfig.class.getName());
    private static HikariDataSource dataSource;

    static {
        initDataSource();
    }

    private static void initDataSource() {
        try {
            logger.info("initializing hikaripc"); 

            Class.forName("org.postgresql.Driver");

            HikariConfig config = new HikariConfig();

            config.setDriverClassName("org.postgresql.Driver"); 
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/studs");
            config.setUsername(getEnv("DB_USERNAME"));
            config.setPassword(getEnv("DB_PASSWORD"));
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
            
            logger.info("HikariCP initialized!");
            logger.info("URL: " + config.getJdbcUrl());
            
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static String getEnv(String key) {
        return System.getenv(key);
    }


}
