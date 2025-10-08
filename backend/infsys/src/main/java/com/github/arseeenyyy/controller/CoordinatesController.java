package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.CoordinatesRequestDto;
import com.github.arseeenyyy.dto.CoordinatesResponseDto;
import com.github.arseeenyyy.service.CoordinatesService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesController {
    
    @Inject
    private CoordinatesService coordinatesService;
    
    @POST
    public Response create(@Valid CoordinatesRequestDto requestDto) {
        try {
            CoordinatesResponseDto response = coordinatesService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating coordinates: " + e.getMessage())
                    .build();
        }
    }
    


    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("coordinates working mathafacka").build();
    }
}