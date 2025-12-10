package com.github.arseeenyyy.cache;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import com.github.arseeenyyy.config.DatabaseManager;

import java.util.logging.Logger;

@Interceptor
@CacheStatisticsLogging
public class CacheStatisticsInterceptor {
    
    private static final Logger logger = Logger.getLogger(CacheStatisticsInterceptor.class.getName());
    
    @Inject
    private CacheStatisticsManager statisticsManager;
    
    @AroundInvoke
    public Object logCacheStatistics(InvocationContext context) throws Exception {
        boolean shouldLog = statisticsManager.isStatisticsLoggingEnabled();
        
        if (!shouldLog) {
            return context.proceed();
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = context.proceed();
            return result;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            logCacheStats(context.getMethod().getName(), executionTime);
        }
    }
    
    private void logCacheStats(String methodName, long executionTime) {
        try {
            Session session = DatabaseManager.getEntityManager()
                    .unwrap(Session.class);
            
            Statistics stats = session.getSessionFactory().getStatistics();
            
            logger.info("=== Cache Stats for method: " + methodName + " ===");
            logger.info("L2 Cache Hits: " + stats.getSecondLevelCacheHitCount());
            logger.info("L2 Cache Misses: " + stats.getSecondLevelCacheMissCount());
            logger.info("Query Cache Hits: " + stats.getQueryCacheHitCount());
            logger.info("Query Cache Misses: " + stats.getQueryCacheMissCount());
            logger.info("Execution time: " + executionTime + " ms");
            logger.info("==============================================");
            
        } catch (Exception e) {
            logger.warning("Failed to get cache stats: " + e.getMessage());
        }
    }
}