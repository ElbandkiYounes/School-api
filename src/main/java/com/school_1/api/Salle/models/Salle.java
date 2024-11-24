package com.school_1.api.Salle.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Salle name cannot be empty")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Location cannot be empty")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Number of seats cannot be null")
    @Column(nullable = false)
    private Integer numberOfSeats;

    @NotNull(message = "Salle type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalleType type;

    // Constructors, getters, and setters
    public Salle() {
    }

    public Salle(String name, String location, Integer numberOfSeats, SalleType type) {
        this.name = name;
        this.location = location;
        this.numberOfSeats = numberOfSeats;
        this.type = type;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Salle name cannot be empty") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Salle name cannot be empty") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Location cannot be empty") String getLocation() {
        return location;
    }

    public void setLocation(@NotBlank(message = "Location cannot be empty") String location) {
        this.location = location;
    }

    public @NotNull(message = "Number of seats cannot be null") Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(@NotNull(message = "Number of seats cannot be null") Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public @NotNull(message = "Salle type cannot be null") SalleType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Salle type cannot be null") SalleType type) {
        this.type = type;
    }
}