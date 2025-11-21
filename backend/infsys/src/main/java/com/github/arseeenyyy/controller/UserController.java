package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.user.LoginRequestDto;
import com.github.arseeenyyy.dto.user.UserRequestDto;
import com.github.arseeenyyy.dto.user.UserResponseDto;
import com.github.arseeenyyy.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(@Valid UserRequestDto requestDto) {
        try {
            UserResponseDto response = userService.register(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating user: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequestDto loginRequest) {
        try {
            UserResponseDto response = userService.login(loginRequest);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authentication failed: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting user: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("working mathafaka").build();
    }
}