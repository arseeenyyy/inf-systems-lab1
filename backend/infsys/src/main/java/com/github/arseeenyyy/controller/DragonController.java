package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.DragonRequestDto;
import com.github.arseeenyyy.dto.DragonResponseDto;
import com.github.arseeenyyy.service.DragonService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/dragons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DragonController {
    
    @Inject
    private DragonService dragonService;
    
    @GET
    public List<DragonResponseDto> getAll() {
        return dragonService.getAll();
    }
    
    @GET
    @Path("/{id}")
    public DragonResponseDto getById(@PathParam("id") Long id) {
        return dragonService.getById(id);
    }
    
    @POST
    public Response create(@Valid DragonRequestDto requestDto) {
        try {
            DragonResponseDto response = dragonService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating dragon: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, DragonRequestDto requestDto) {
        try {
            DragonResponseDto response = dragonService.update(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating dragon: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            dragonService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragon: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/color/{color}")
    public List<DragonResponseDto> findByColor(@PathParam("color") String color) {
        return dragonService.findByColor(color);
    }
    @DELETE
    @Path("/color/{color}/all")
    public Response deleteAllByColor(@PathParam("color") String color) {
        try {
            dragonService.deleteAllByColor(color);
            return Response.ok("All dragons with color " + color + " have been deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragons by color: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/color/{color}/one")
    public Response deleteOneByColor(@PathParam("color") String color) {
        try {
            dragonService.deleteOneByColor(color);
            return Response.ok("One dragon with color " + color + " has been deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting one dragon by color: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/name-starts-with/{substring}")
    public List<DragonResponseDto> findByNameStartingWith(@PathParam("substring") String substring) {
        return dragonService.findByNameStartingWith(substring);
    }
}