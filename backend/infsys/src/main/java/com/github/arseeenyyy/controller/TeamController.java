package com.github.arseeenyyy.controller;

import java.util.List;

import com.github.arseeenyyy.dto.team.TeamCreateRequestDto;
import com.github.arseeenyyy.dto.team.TeamCreateResponseDto;
import com.github.arseeenyyy.dto.team.TeamDto;
import com.github.arseeenyyy.dto.team.TeamToCaveRequestDto;
import com.github.arseeenyyy.dto.team.TeamToCaveResponseDto;
import com.github.arseeenyyy.models.Team;
import com.github.arseeenyyy.service.TeamService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamController {
    
    @Inject
    private TeamService teamService;
    
    @GET
    public Response getAllTeams(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<TeamDto> teams = teamService.getAllTeams(jwtToken);
            return Response.ok(teams).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting teams: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getTeamById(@PathParam("id") Long id,
                               @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            Team team = teamService.getTeamById(id, jwtToken);
            return Response.ok(team).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error getting team: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    public Response createTeam(@Valid TeamCreateRequestDto requestDto,
                              @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            TeamCreateResponseDto response = teamService.createTeam(requestDto, jwtToken);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating team: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/send-to-cave")
    public Response sendTeamToCave(@Valid TeamToCaveRequestDto requestDto,
                                  @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            TeamToCaveResponseDto response = teamService.sendTeamToCave(requestDto, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error sending team to cave: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/add-members")
    public Response addMembersToTeam(@PathParam("id") Long id, 
                                    List<Long> personIds,
                                    @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            TeamCreateResponseDto response = teamService.addMembersToTeam(id, personIds, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error adding members: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/remove-members")
    public Response removeMembersFromTeam(@PathParam("id") Long id, 
                                         List<Long> personIds,
                                         @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            TeamCreateResponseDto response = teamService.removeMembersFromTeam(id, personIds, jwtToken);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error removing members: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") Long id,
                              @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            teamService.deleteTeam(id, jwtToken);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error deleting team: " + e.getMessage())
                    .build();
        }
    }

    private String extractToken(String authHeader) {
        return authHeader.substring("Bearer ".length()).trim();
    }
}