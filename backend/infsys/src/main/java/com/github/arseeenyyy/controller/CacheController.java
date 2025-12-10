package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.cache.CacheStatisticsManager;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/cache")
@Produces(MediaType.APPLICATION_JSON)
public class CacheController {
    
    @Inject
    private CacheStatisticsManager cacheStatisticsManager;
    
    @GET
    @Path("/statistics/status")
    public Response getStatisticsStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("statisticsLoggingEnabled", cacheStatisticsManager.isStatisticsLoggingEnabled());
        return Response.ok(response).build();
    }
    
    @POST
    @Path("/statistics/enable")
    public Response enableStatistics() {
        cacheStatisticsManager.enableStatisticsLogging();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hibernate statistics logging enabled");
        response.put("status", "enabled");
        response.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(response).build();
    }
    
    @POST
    @Path("/statistics/disable")
    public Response disableStatistics() {
        cacheStatisticsManager.disableStatisticsLogging();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hibernate statistics logging disabled");
        response.put("status", "disabled");
        response.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(response).build();
    }
    
    // @GET
    // @Path("/hibernate-stats")
    // public Response getHibernateStatistics() {
    //     try {
    //         Statistics stats = DatabaseManager.getStatistics();
            
    //         if (stats == null) {
    //             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    //                     .entity(Map.of("error", "Cannot get statistics"))
    //                     .build();
    //         }
            
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("statisticsEnabled", stats.isStatisticsEnabled());
            
    //         Map<String, Object> cacheStats = new HashMap<>();
    //         cacheStats.put("l2CacheHits", stats.getSecondLevelCacheHitCount());
    //         cacheStats.put("l2CacheMisses", stats.getSecondLevelCacheMissCount());
    //         cacheStats.put("l2CachePuts", stats.getSecondLevelCachePutCount());
            
    //         cacheStats.put("queryCacheHits", stats.getQueryCacheHitCount());
    //         cacheStats.put("queryCacheMisses", stats.getQueryCacheMissCount());
    //         cacheStats.put("queryCachePuts", stats.getQueryCachePutCount());
            
    //         response.put("cacheStatistics", cacheStats);
            
    //         return Response.ok(response).build();
            
    //     } catch (Exception e) {
    //         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    //                 .entity(Map.of("error", e.getMessage()))
    //                 .build();
    //     }
    // }
}