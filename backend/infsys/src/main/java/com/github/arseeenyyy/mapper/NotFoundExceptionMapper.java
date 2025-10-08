package com.github.arseeenyyy.mapper;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", exception.getMessage());
        response.put("error", "NOT_FOUND");
        
        return Response.status(Response.Status.NOT_FOUND)
                .entity(response)
                .build();
    }
}