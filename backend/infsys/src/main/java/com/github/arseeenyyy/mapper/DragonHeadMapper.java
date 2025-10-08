package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.DragonHeadResponseDto;
import com.github.arseeenyyy.models.DragonHead;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DragonHeadMapper {

    public static DragonHead toEntity(DragonHeadRequestDto requestDto) {
        DragonHead head = new DragonHead();
        head.setEyesCount(requestDto.getEyesCount()); 
        head.setSize(requestDto.getSize());
        return head;
    }

    public static DragonHeadResponseDto toResponseDto(DragonHead head) {
        return new DragonHeadResponseDto(
            head.getId(), 
            head.getEyesCount(), 
            head.getSize()
        );
    }
}