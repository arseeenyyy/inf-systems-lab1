package com.github.arseeenyyy.cache;

import com.github.arseeenyyy.config.DatabaseManager;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheStatisticsManager {
    
    public boolean isStatisticsLoggingEnabled() {
        return DatabaseManager.isStatisticsEnabled();
    }
    
    public void enableStatisticsLogging() {
        DatabaseManager.setStatisticsEnabled(true);
    }
    
    public void disableStatisticsLogging() {
        DatabaseManager.setStatisticsEnabled(false);
    }
}