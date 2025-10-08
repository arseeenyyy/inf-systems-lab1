package com.github.arseeenyyy.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "location") 
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "x", nullable = false) 
    private Integer x; //Поле не может быть null

    @Column(name = "y") 
    private float y;

    @Column(name = "z")
    private long z;
    
    @Column(name = "name") 
    @NotBlank
    private String name; //Строка не может быть пустой, Поле может быть null
}