package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.location.LocationRequestDto;
import com.github.arseeenyyy.dto.location.LocationResponseDto;
import com.github.arseeenyyy.service.LocationService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationController {
    
    @Inject
    private LocationService locationService;
    
    @GET
    public Response getAllLocations(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<LocationResponseDto> locations = locationService.getAllLocations(jwtToken);
            return Response.ok(locations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting locations: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getLocation(@PathParam("id") Long id,
                               @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            LocationResponseDto location = locationService.getLocationById(id, jwtToken);
            return Response.ok(location).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting location: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    public Response createLocation(@Valid LocationRequestDto requestDto,
                                  @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            LocationResponseDto response = locationService.createLocation(requestDto, jwtToken);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating location: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateLocation(@PathParam("id") Long id, 
                                  @Valid LocationRequestDto requestDto,
                                  @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            LocationResponseDto response = locationService.updateLocation(id, requestDto, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating location: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteLocation(@PathParam("id") Long id,
                                  @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            locationService.deleteLocation(id, jwtToken);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting location: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("Test works!").build();
    }

    private String extractToken(String authHeader) {
        return authHeader.substring("Bearer ".length()).trim();
    }
}