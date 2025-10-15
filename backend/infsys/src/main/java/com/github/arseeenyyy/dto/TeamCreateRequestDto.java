package com.github.arseeenyyy.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDto {
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(min = 1) 
    private List<Long> personsIds;
}
