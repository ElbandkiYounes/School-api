package com.school_1.api.Reservation.models;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.User.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReservationPayload {

    @NotNull(message = "Reservation Jour cannot be null")
    private Jour jour;

    @NotNull(message = "Reservation Week cannot be null")
    private Week week;

    @NotNull(message = "Reservation Seance cannot be null")
    private Seance seance;

    @NotNull(message = "Reservation salle cannot be null")
    private Long salleId;

    @NotNull(message = "Reservation filiere cannot be null")
    private Long filiereId;

    public Jour getJour() {
        return jour;
    }

    public void setJour( Jour jour) {
        this.jour = jour;
    }

    public  Week getWeek() {
        return week;
    }

    public void setWeek( Week week) {
        this.week = week;
    }

    public  Seance getSeance() {
        return seance;
    }

    public void setSeance( Seance seance) {
        this.seance = seance;
    }

    public  Long getSalleId() {
        return salleId;
    }

    public void setSalleId( Long salleId) {
        this.salleId = salleId;
    }

    public  Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId( Long filiereId) {
        this.filiereId = filiereId;
    }

    public Reservation toReservation(ReservationStatus reservationStatus, User professeur, Salle salle, Filiere filiere) {
        return new Reservation(professeur, jour, week, seance, salle, filiere, reservationStatus);
    }
}
