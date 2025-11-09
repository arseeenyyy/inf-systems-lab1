package com.github.arseeenyyy.dto.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.github.arseeenyyy.dto.person.PersonResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {
    private Long id;
    private String name;
    private Integer memberCount;
    private List<PersonResponseDto> members; 
}