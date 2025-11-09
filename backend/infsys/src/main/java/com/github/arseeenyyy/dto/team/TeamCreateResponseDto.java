package com.github.arseeenyyy.dto.team;

import java.util.List;

import com.github.arseeenyyy.dto.person.PersonResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateResponseDto {
    private Long teamId;    
    private Integer memberCount;
    private List<PersonResponseDto> members; 
}
