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
    
    private long totalL2Hits = 0;
    private long totalL2Misses = 0;
    private long totalQueryHits = 0;
    private long totalQueryMisses = 0;
    
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
            
            long currentL2Hits = stats.getSecondLevelCacheHitCount();
            long currentL2Misses = stats.getSecondLevelCacheMissCount();
            long currentQueryHits = stats.getQueryCacheHitCount();
            long currentQueryMisses = stats.getQueryCacheMissCount();
            
            totalL2Hits += currentL2Hits;
            totalL2Misses += currentL2Misses;
            totalQueryHits += currentQueryHits;
            totalQueryMisses += currentQueryMisses;
            
            logger.info("=== Cache Stats for method: " + methodName + " ===");
            logger.info("L2 Cache Hits: " + totalL2Hits);
            logger.info("L2 Cache Misses: " + totalL2Misses);
            logger.info("Query Cache Hits: " + totalQueryHits);
            logger.info("Query Cache Misses: " + totalQueryMisses);
            logger.info("Execution time: " + executionTime + " ms");
            logger.info("==============================================");
            
        } catch (Exception e) {
            logger.warning("Failed to get cache stats: " + e.getMessage());
        }
    }
}