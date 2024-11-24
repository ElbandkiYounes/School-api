package com.school_1.api.Filiere.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateFilierePayload {

    @NotBlank(message = "Filiere name cannot be empty")
    private String name;

    @NotNull(message = "Capacity cannot be null")
    private Integer capacity;


    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Filiere toFiliere(Filiere filiere) {
        filiere.setName(this.name);
        filiere.setCapacity(this.capacity);
        return filiere;
    }
}