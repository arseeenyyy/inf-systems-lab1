package com.github.arseeenyyy.dto.coordinates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesResponseDto {
    private long id;
    private double x; 
    private double y;
}