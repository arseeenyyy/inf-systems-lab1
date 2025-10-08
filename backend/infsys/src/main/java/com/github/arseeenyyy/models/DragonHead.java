package com.github.arseeenyyy.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dragon_head") 
public class DragonHead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private long id;

    @Column(name = "size") 
    private int size;

    @Column(name = "eyes_count")
    private Integer eyesCount; //Поле может быть null
}
