package com.school_1.api.EmploiDuTemps.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.Salle.models.SalleType;
import com.school_1.api.User.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Table(name = "emploi_du_temps")
public class EmploiDuTemps implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Jour cannot be null")
    private Jour jour;

    @NotNull(message = "Seance cannot be null")
    private Seance seance;

    @NotNull(message = "TypeSeance cannot be null")
    private SalleType typeSeance;

    @ManyToOne
    @JoinColumn(name = "chargeHoraire_id")
    @NotNull(message = "ChargeHoraire cannot be null")
    private ChargeHoraire chargeHoraire;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    @NotNull(message = "Salle cannot be null")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "professeur_id")
    @NotNull(message = "Professeur cannot be null")
    private User professeur;


    // Constructors
    public EmploiDuTemps() {
    }

    public EmploiDuTemps(Jour jour, Seance seance, SalleType typeSeance, ChargeHoraire chargeHoraire, Salle salle, User professeur) {
        this.jour = jour;
        this.seance = seance;
        this.typeSeance = typeSeance;
        this.chargeHoraire = chargeHoraire;
        this.salle = salle;
        this.professeur = professeur;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jour getJour() {
        return jour;
    }

    public void setJour(Jour jour) {
        this.jour = jour;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public ChargeHoraire getChargeHoraire() {
        return chargeHoraire;
    }

    public void setChargeHoraire(ChargeHoraire chargeHoraire) {
        this.chargeHoraire = chargeHoraire;
    }


    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public User getUser() {
        return professeur;
    }

    public void setUser(User professeur) {
        this.professeur = professeur;
    }

    public SalleType getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(SalleType typeSeance) {
        this.typeSeance = typeSeance;
    }
}
