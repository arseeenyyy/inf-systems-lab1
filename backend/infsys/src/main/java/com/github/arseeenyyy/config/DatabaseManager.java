package com.github.arseeenyyy.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DatabaseManager {
    
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static HikariDataSource dataSource;
    private static EntityManagerFactory entityManagerFactory;
    private static boolean statisticsEnabled = false;
    
    static {
        initDataSource();
        initEntityManagerFactory();
    }
    
    private static void initDataSource() {
        try {
            logger.info("Initializing HikariCP DataSource...");
            
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/studs");
            config.setUsername(System.getenv("DB_USER"));
            config.setPassword(System.getenv("DB_PASSWORD"));
            config.setDriverClassName("org.postgresql.Driver");
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP DataSource initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to initialize HikariCP: " + e.getMessage());
            throw new RuntimeException("HikariCP initialization failed", e);
        }
    }
    
    private static void initEntityManagerFactory() {
        try {
            logger.info("Initializing EntityManagerFactory...");
            
            Map<String, Object> properties = new HashMap<>();
            
            properties.put("jakarta.persistence.nonJtaDataSource", dataSource);
            
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "true");
            properties.put("hibernate.format_sql", "true");
            
            properties.put("hibernate.generate_statistics", "true");
            
            properties.put("hibernate.cache.use_second_level_cache", "true");
            properties.put("hibernate.cache.use_query_cache", "true");
            
            entityManagerFactory = Persistence.createEntityManagerFactory("default", properties);
            logger.info("EntityManagerFactory initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to initialize EntityManagerFactory: " + e.getMessage());
            throw new RuntimeException("EntityManagerFactory initialization failed", e);
        }
    }
    
    public static synchronized void setStatisticsEnabled(boolean enabled) {
        statisticsEnabled = enabled;
    }
    
    public static boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
    
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    
    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            logger.info("EntityManagerFactory closed");
        }
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP DataSource closed");
        }
    }
}