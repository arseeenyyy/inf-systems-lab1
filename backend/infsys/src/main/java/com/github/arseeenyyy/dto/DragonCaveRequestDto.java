package com.github.arseeenyyy.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DragonCaveRequestDto {
    @Min(1) 
    private long numberOfTreasures;    
}