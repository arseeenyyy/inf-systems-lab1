package com.github.arseeenyyy.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "person") 
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false) 
    @NotBlank
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Enumerated(EnumType.STRING) 
    @Column(name = "eye_color", nullable = false) 
    private Color eyeColor; //Поле не может быть null

    @Enumerated(EnumType.STRING) 
    @Column(name = "hair_color") 
    private Color hairColor; //Поле может быть null

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "location_id") 
    private Location location; //Поле может быть null
    
    @Column(name = "height", nullable = false) 
    @Positive
    private int height; //Значение поля должно быть больше 0
    
    @Enumerated(EnumType.STRING) 
    private Country nationality; //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne 
    @JoinColumn(name = "user_id", nullable = false) 
    private User user; 
}