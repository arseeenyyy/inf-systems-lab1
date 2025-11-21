package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.coordinates.CoordinatesRequestDto;
import com.github.arseeenyyy.dto.coordinates.CoordinatesResponseDto;
import com.github.arseeenyyy.service.CoordinatesService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesController {
    
    @Inject
    private CoordinatesService coordinatesService;
    
    @GET
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<CoordinatesResponseDto> coordinates = coordinatesService.getAll(jwtToken);
            return Response.ok(coordinates).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting coordinates: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id, 
                           @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            CoordinatesResponseDto coordinates = coordinatesService.getById(id, jwtToken);
            return Response.ok(coordinates).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting coordinates: " + e.getMessage())
                    .build();
        }
    }

    @POST
    public Response create(@Valid CoordinatesRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            CoordinatesResponseDto response = coordinatesService.create(requestDto, jwtToken);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating coordinates: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, 
                          @Valid CoordinatesRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            CoordinatesResponseDto response = coordinatesService.update(id, requestDto, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating coordinates: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            coordinatesService.delete(id, jwtToken);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting coordinates: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("coordinates working mathafacka").build();
    }

    private String extractToken(String authHeader) {
        return authHeader.substring("Bearer ".length()).trim();
    }
}