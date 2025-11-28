package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.dragonHead.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadResponseDto;
import com.github.arseeenyyy.service.DragonHeadService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/heads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DragonHeadController {
    
    @Inject
    private DragonHeadService headService;
    
    @GET
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<DragonHeadResponseDto> heads = headService.getAll(jwtToken);
            return Response.ok(heads).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting heads: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id, 
                           @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonHeadResponseDto head = headService.getById(id, jwtToken);
            return Response.ok(head).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting head: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    public Response create(@Valid DragonHeadRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonHeadResponseDto response = headService.create(requestDto, jwtToken);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating dragon head: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, 
                          @Valid DragonHeadRequestDto requestDto,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            DragonHeadResponseDto response = headService.update(id, requestDto, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating dragon head: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,
                          @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            headService.delete(id, jwtToken);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting dragon head: " + e.getMessage())
                    .build();
        }
    }

    private String extractToken(String authHeader) {
        return authHeader.substring("Bearer ".length()).trim();
    }
}