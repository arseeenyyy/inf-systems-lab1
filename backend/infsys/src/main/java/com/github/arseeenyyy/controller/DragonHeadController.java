package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.dragonHead.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadResponseDto;
import com.github.arseeenyyy.service.DragonHeadService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/heads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DragonHeadController {
    
    @Inject
    private DragonHeadService headService;
    
    @GET
    public List<DragonHeadResponseDto> getAll() {
        return headService.getAll();
    }
    
    @GET
    @Path("/{id}")
    public DragonHeadResponseDto getById(@PathParam("id") Long id) {
        return headService.getById(id);
    }
    
    @POST
    public Response create(@Valid DragonHeadRequestDto requestDto) {
        try {
            DragonHeadResponseDto response = headService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating dragon head: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, DragonHeadRequestDto requestDto) {
        try {
            DragonHeadResponseDto response = headService.update(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating dragon head: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            headService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragon head: " + e.getMessage())
                    .build();
        }
    }
}