package com.github.arseeenyyy.dto.dragonCave;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DragonCaveRequestDto {
    @NotNull(message = "number of treasures is required")
    @Min(value = 1, message = "number of treasures min value is 1") 
    private long numberOfTreasures;    
}