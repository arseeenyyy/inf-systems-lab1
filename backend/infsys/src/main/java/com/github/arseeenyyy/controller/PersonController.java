package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.PersonRequestDto;
import com.github.arseeenyyy.dto.PersonResponseDto;
import com.github.arseeenyyy.service.PersonService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController {
    
    @Inject
    private PersonService personService;
    
    @GET
    public List<PersonResponseDto> getAll() {
        return personService.getAll();
    }
    
    @GET
    @Path("/{id}")
    public PersonResponseDto getById(@PathParam("id") Long id) {
        return personService.getById(id);
    }
    
    @POST
    public Response create(@Valid PersonRequestDto requestDto) {
        try {
            PersonResponseDto response = personService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating person: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, PersonRequestDto requestDto) {
        try {
            PersonResponseDto response = personService.update(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating person: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            personService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting person: " + e.getMessage())
                    .build();
        }
    }
}