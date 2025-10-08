package com.github.arseeenyyy.dto;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.Country;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {
    @NotNull
    @NotBlank
    private String name;   

    @NotNull
    private Color eyeColor;
    private Color hairColor;
    private Long locationId;

    @Min(1) 
    private int height;
    private Country nationality;
}
