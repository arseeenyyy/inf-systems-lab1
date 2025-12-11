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
        
        try {
            String jwtToken = extractToken(authHeader);
            
            Map<String, List<InputPart>> formData = input.getFormDataMap();
            List<InputPart> fileParts = formData.get("file");
            
            if (fileParts == null || fileParts.isEmpty()) {
                ImportOperation result = importService.createFailedOperation("File not provided", jwtToken);
                return Response.ok(createResponse(result)).build();
            }
            
            InputPart filePart = fileParts.get(0);
            InputStream fileStream = filePart.getBody(InputStream.class, null);
            String fileName = getFileName(filePart);
            
            // ДВУХФАЗНЫЙ КОММИТ
            ImportOperation result = importService.processImport(fileStream, fileName, jwtToken);
            return Response.ok(createResponse(result)).build();
            
        } catch (RuntimeException e) {
            // MinIO недоступен - возвращаем 500, запись в БД НЕ создана
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "MinIO недоступен: " + e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/history")
    public Response getHistory(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String jwtToken = extractToken(authHeader);
            List<ImportOperation> history = importService.getImportHistory(jwtToken);
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") Long importId,
                                @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        
        try {
            String jwtToken = extractToken(authHeader);
            InputStream fileStream = importService.downloadImportFile(importId, jwtToken);
            
            return Response.ok(fileStream)
                .header("Content-Disposition", "attachment; filename=\"import_" + importId + "\"")
                .header("Content-Type", "application/octet-stream")
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    private Map<String, Object> createResponse(ImportOperation result) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", result.getId());
        response.put("status", result.getStatus().toString());
        response.put("addedCount", result.getAddedCount());
        response.put("hasFile", result.getFileKey() != null);
        
        if (result.getErrorMessage() != null) {
            response.put("error", result.getErrorMessage());
        }
        
        return response;
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        return authHeader.substring("Bearer ".length()).trim();
    }
    
    private String getFileName(InputPart part) {
        String header = part.getHeaders().getFirst("Content-Disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.split("=")[1].trim().replace("\"", "");
            }
        }
        return "file";
    }
}