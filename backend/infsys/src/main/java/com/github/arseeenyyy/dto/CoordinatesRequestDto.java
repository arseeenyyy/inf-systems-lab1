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
    @NotNull
    @Max(696)
    private double x; 

    @NotNull
    @Max(366)
    private double y;
}