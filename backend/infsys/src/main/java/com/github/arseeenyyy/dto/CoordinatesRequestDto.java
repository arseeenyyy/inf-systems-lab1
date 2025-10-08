package com.github.arseeenyyy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesRequestDto {
    @NotNull(message = "X coordinate is required")
    @Max(value = 696, message = "X coordinate cannot exceed 696")
    private Double x; 

    @NotNull(message = "Y coordinate is required")
    @Max(value = 366, message = "Y coordinate cannot exceed 366")
    private Double y; 
}