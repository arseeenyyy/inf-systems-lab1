package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.user.AuthResponseDto;
import com.github.arseeenyyy.dto.user.LoginRequestDto;
import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.dto.user.UserResponseDto;
import com.github.arseeenyyy.service.UserService;

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

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    @Inject
    private UserService userService;
    
    @GET
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }
    
    @GET
    @Path("/{id}")
    public UserResponseDto getById(@PathParam("id") Long id) {
        return userService.getById(id);
    }

    @POST
    public Response create(@Valid UserRequestDto requestDto) {
        try {
            UserResponseDto response = userService.create(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating user: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UserRequestDto requestDto) {
        try {
            UserResponseDto response = userService.update(id, requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating user: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            userService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting user: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/auth/login")
    public Response login(@Valid LoginRequestDto loginRequest) {
        try {
            AuthResponseDto authResponse = userService.authenticate(loginRequest);
            
            if (authResponse.getJwt() == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(authResponse)
                        .build();
            }
            
            return Response.ok(authResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Authentication error: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/auth/validate")
    public Response validateToken(String token) {
        try {
            boolean isValid = userService.validateToken(token);
            return Response.ok().entity("{\"valid\": " + isValid + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Token validation error: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("test users working mathafaka").build();
    }
}