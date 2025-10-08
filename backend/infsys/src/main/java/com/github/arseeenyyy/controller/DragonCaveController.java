package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.DragonCaveRequestDto;
import com.github.arseeenyyy.dto.DragonCaveResponseDto;
import com.github.arseeenyyy.service.DragonCaveService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/caves")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DragonCaveController {
    
    @Inject
    private DragonCaveService caveService;
    
    @GET
    public List<DragonCaveResponseDto> getAll() {
        return caveService.getAll();
    }
    
    @GET
    @Path("/{id}")
    public DragonCaveResponseDto getById(@PathParam("id") Long id) {
        return caveService.getById(id);
    }
    
    @POST
    public Response create(DragonCaveRequestDto requestDto) {
        try {
            DragonCaveResponseDto response = caveService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating dragon cave: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, DragonCaveRequestDto requestDto) {
        try {
            DragonCaveResponseDto response = caveService.update(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating dragon cave: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            caveService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragon cave: " + e.getMessage())
                    .build();
        }
    }
}