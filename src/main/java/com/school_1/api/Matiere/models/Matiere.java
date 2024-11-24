package com.school_1.api.Matiere.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matieres")
public class Matiere implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nom de la matière ne peut pas être vide")
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "matiere", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChargeHoraire> chargesHoraires = new ArrayList<>();

    // Constructors
    public Matiere() {
    }

    public Matiere(String name) {
        this.name = name;
        this.chargesHoraires = new ArrayList<>();
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

    public List<ChargeHoraire> getChargesHoraires() {
        return chargesHoraires;
    }

    public void setChargesHoraires(List<ChargeHoraire> chargesHoraires) {
        this.chargesHoraires = chargesHoraires;
    }
}