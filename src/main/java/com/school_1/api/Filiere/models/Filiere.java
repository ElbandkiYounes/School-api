package com.school_1.api.Filiere.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filieres")
public class Filiere implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Filiere name cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Capacity cannot be null")
    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChargeHoraire> chargeHoraires = new ArrayList<>();

    // Constructors
    public Filiere() {
    }

    public Filiere(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
        this.chargeHoraires = new ArrayList<>();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<ChargeHoraire> getChargeHoraires() {
        return chargeHoraires != null ? chargeHoraires : new ArrayList<>();
    }

    public void setChargeHoraires(List<ChargeHoraire> chargeHoraires) {
        this.chargeHoraires = chargeHoraires != null ? chargeHoraires : new ArrayList<>();
    }
}