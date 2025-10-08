package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.LocationRequestDto;
import com.github.arseeenyyy.dto.LocationResponseDto;
import com.github.arseeenyyy.service.LocationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationController {
    
    @Inject
    private LocationService locationService;
    
    @GET
    public List<LocationResponseDto> getAllLocations() {
        return locationService.getAllLocations();
    }
    
    @GET
    @Path("/{id}")
    public LocationResponseDto getLocation(@PathParam("id") Long id) {
        return locationService.getLocationById(id);
    }
    
    @POST
    public Response createLocation(@Valid LocationRequestDto requestDto) {
        try {
            LocationResponseDto response = locationService.createLocation(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating location: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateLocation(@PathParam("id") Long id, LocationRequestDto requestDto) {
        try {
            LocationResponseDto response = locationService.updateLocation(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating location: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteLocation(@PathParam("id") Long id) {
        try {
            locationService.deleteLocation(id);
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
}