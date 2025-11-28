package com.github.arseeenyyy.mapper;

import com.github.arseeenyyy.dto.dragonHead.DragonHeadRequestDto;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadResponseDto;
import com.github.arseeenyyy.models.DragonHead;
import com.github.arseeenyyy.models.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DragonHeadMapper {

    public static DragonHead toEntity(DragonHeadRequestDto requestDto, User user) {
        DragonHead head = new DragonHead();
        head.setEyesCount(requestDto.getEyesCount()); 
        head.setSize(requestDto.getSize());
        head.setUser(user);
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