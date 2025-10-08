package com.github.arseeenyyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {
    @NotNull(message = "X cannot be null")
    private Integer x;
    
    private float y;
    
    private long z;
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
}