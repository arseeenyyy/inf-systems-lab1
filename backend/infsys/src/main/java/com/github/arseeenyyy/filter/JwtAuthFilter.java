package com.github.arseeenyyy.filter;

import com.github.arseeenyyy.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("/users/register") || 
            path.contains("/users/login") || 
            path.contains("coordinates/test") || 
            path.contains("/cache") || 
            requestContext.getMethod().equals("OPTIONS")) {
            return;
        }

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Missing or invalid Authorization header\"}")
                        .build()
            );
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        
        if (!userService.validateToken(token)) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid or expired token\"}")
                        .build()
            );
        }
    }
}