package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.dragon.DragonRequestDto;
import com.github.arseeenyyy.dto.dragon.DragonResponseDto;
import com.github.arseeenyyy.service.DragonService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
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
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<DragonResponseDto> dragons = dragonService.getAll(jwtToken);
            return Response.ok(dragons).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting dragons: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id, 
                           @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonResponseDto dragon = dragonService.getById(id, jwtToken);
            return Response.ok(dragon).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting dragon: " + e.getMessage())
                    .build();
        }
    }

    @POST
    public Response create(@Valid DragonRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonResponseDto response = dragonService.create(requestDto, jwtToken);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating dragon: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, 
                          @Valid DragonRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonResponseDto response = dragonService.update(id, requestDto, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating dragon: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            dragonService.delete(id, jwtToken);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragon: " + e.getMessage())
                    .build();
        }
    }

    private String extractToken(String authHeader) {
        return authHeader.substring("Bearer ".length()).trim();
    }
}