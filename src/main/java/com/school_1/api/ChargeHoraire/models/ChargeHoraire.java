package com.school_1.api.ChargeHoraire.models;

import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Matiere.models.Matiere;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Table(name = "charges_horaires")

public class ChargeHoraire implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Filière ne peut pas être nulle")
    @ManyToOne()
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @NotNull(message = "Matière ne peut pas être nulle")
    @ManyToOne()
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @NotNull(message = "Heures de cours ne peuvent pas être nulles")
    @Column(nullable = false)
    private Integer heuresCours;

    @NotNull(message = "Heures de TP ne peuvent pas être nulles")
    @Column(nullable = false)
    private Integer heuresTP;

    @NotNull(message = "Heures de TD ne peuvent pas être nulles")
    @Column(nullable = false)
    private Integer heuresTD;

    // Constructors
    public ChargeHoraire() {
    }

    public ChargeHoraire(Filiere filiere, Matiere matiere, Integer heuresCours, Integer heuresTP, Integer heuresTD) {
        this.filiere = filiere;
        this.matiere = matiere;
        this.heuresCours = heuresCours;
        this.heuresTP = heuresTP;
        this.heuresTD = heuresTD;
    }
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public Integer getHeuresCours() {
        return heuresCours;
    }

    public void setHeuresCours(Integer heuresCours) {
        this.heuresCours = heuresCours;
    }

    public Integer getHeuresTP() {
        return heuresTP;
    }

    public void setHeuresTP(Integer heuresTP) {
        this.heuresTP = heuresTP;
    }

    public Integer getHeuresTD() {
        return heuresTD;
    }

    public void setHeuresTD(Integer heuresTD) {
        this.heuresTD = heuresTD;
    }
}