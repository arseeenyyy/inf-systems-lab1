package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.dto.TeamCreateRequestDto;
import com.github.arseeenyyy.dto.TeamCreateResponseDto;
import com.github.arseeenyyy.dto.TeamToCaveRequestDto;
import com.github.arseeenyyy.dto.TeamToCaveResponseDto;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.service.TeamService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamController {
    
    @Inject
    private TeamService teamService;
    
    @GET
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }
    
    @GET
    @Path("/{id}")
    public Team getTeamById(@PathParam("id") Long id) {
        return teamService.getTeamById(id);
    }
    
    @POST
    public Response createTeam(@Valid TeamCreateRequestDto requestDto) {
        try {
            TeamCreateResponseDto response = teamService.createTeam(requestDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating team: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/send-to-cave")
    public Response sendTeamToCave(@Valid TeamToCaveRequestDto requestDto) {
        try {
            TeamToCaveResponseDto response = teamService.sendTeamToCave(requestDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error sending team to cave: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/add-members")
    public Response addMembersToTeam(@PathParam("id") Long id, List<Long> personIds) {
        try {
            TeamCreateResponseDto response = teamService.addMembersToTeam(id, personIds);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error adding members: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/remove-members")
    public Response removeMembersFromTeam(@PathParam("id") Long id, List<Long> personIds) {
        try {
            TeamCreateResponseDto response = teamService.removeMembersFromTeam(id, personIds);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error removing members: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") Long id) {
        try {
            teamService.deleteTeam(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting team: " + e.getMessage())
                    .build();
        }
    }
}