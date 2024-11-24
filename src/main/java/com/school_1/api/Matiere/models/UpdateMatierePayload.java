package com.school_1.api.Matiere.models;

import jakarta.validation.constraints.NotBlank;

public class UpdateMatierePayload {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matiere toMatiere(Matiere matiere) {
        matiere.setName(name.trim());
        return matiere;
    }
}