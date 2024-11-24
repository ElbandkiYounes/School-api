package com.school_1.api.Salle.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateSallePayload {

    @NotBlank(message = "Salle name cannot be empty")
    private String name;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Number of seats cannot be null")
    private Integer numberOfSeats;

    @NotNull(message = "Salle type cannot be null")
    private SalleType type;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public SalleType getType() {
        return type;
    }

    public void setType(SalleType type) {
        this.type = type;
    }

    public Salle toSalle(Salle salle) {
        salle.setName(name.trim());
        salle.setLocation(location.trim());
        salle.setNumberOfSeats(numberOfSeats);
        salle.setType(type);
        return salle;
    }
}