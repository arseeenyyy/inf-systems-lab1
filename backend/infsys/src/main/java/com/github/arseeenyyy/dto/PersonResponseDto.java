package com.github.arseeenyyy.dto;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.Country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class PersonResponseDto {
    private long id;
    private String name;
    private Color eyeColor;
    private Color hairColor;
    private LocationResponseDto location;
    private int height;
    private Country nationality;
}