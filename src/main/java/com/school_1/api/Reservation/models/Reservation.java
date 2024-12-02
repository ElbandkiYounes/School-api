package com.school_1.api.Reservation.models;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.User.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professeur_id")
    @NotNull(message = "Reservation professeur cannot be null")
    private User professeur;

    @NotNull(message = "Reservation Jour cannot be null")
    private Jour jour;

    @NotNull(message = "Reservation Week cannot be null")
    private Week week;

    @NotNull(message = "Reservation Seance cannot be null")
    private Seance seance;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    @NotNull(message = "Reservation salle cannot be null")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    @NotNull(message = "Reservation filiere cannot be null")
    private Filiere filiere;

    @NotNull(message = "Reservation status cannot be null")
    private ReservationStatus reservationStatus;

    // Constructors
    public Reservation() {
    }

    public Reservation(User professeur, Jour jour, Week week, Seance seance, Salle salle, Filiere filiere, ReservationStatus reservationStatus) {
        this.professeur = professeur;
        this.jour = jour;
        this.week = week;
        this.seance = seance;
        this.salle = salle;
        this.filiere = filiere;
        this.reservationStatus = reservationStatus;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getProfesseur() {
        return professeur;
    }

    public void setProfesseur(User professeur) {
        this.professeur = professeur;
    }

    public Jour getJour() {
        return jour;
    }

    public void setJour(Jour jour) {
        this.jour = jour;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}