package com.github.arseeenyyy.controller;

import com.github.arseeenyyy.models.ImportOperation;
import com.github.arseeenyyy.service.ImportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
public class ImportController {

    @Inject
    ImportService importService;

    @POST
    @Path("/dragons")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
                              MultipartFormDataInput input) {
        
        ImportOperation result;
        
        try {
            String jwtToken = extractToken(authHeader);
            
            Map<String, List<InputPart>> formData = input.getFormDataMap();
            List<InputPart> fileParts = formData.get("file");
            
            if (fileParts == null || fileParts.isEmpty()) {
                result = importService.createFailedOperation("File not provided", jwtToken);
            } else {
                InputPart filePart = fileParts.get(0);
                InputStream fileStream = filePart.getBody(InputStream.class, null);
                result = importService.processImport(fileStream, jwtToken);
            }
            
        } catch (Exception e) {
            result = importService.createFailedOperation(e.getMessage(), authHeader);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", result.getStatus().toString());
        response.put("addedCount", result.getAddedCount());
        
        return Response.ok(response).build();
    }

    @GET
    @Path("/history")
    public Response getHistory(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<ImportOperation> history = importService.getImportHistory(jwtToken);
            return Response.ok(history).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        return authHeader.substring("Bearer ".length()).trim();
    }
}