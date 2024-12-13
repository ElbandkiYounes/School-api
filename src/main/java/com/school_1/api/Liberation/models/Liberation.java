package com.school_1.api.Liberation.models;

import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.Reservation.models.Week;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Table(name = "liberations")
public class Liberation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Liberation Week cannot be null")
    private Week week;

    @ManyToOne
    @JoinColumn(name = "emploi_du_temps_id")
    @NotNull(message = "Liberation emploi du temps cannot be null")
    private EmploiDuTemps emploiDuTemps;

    // Constructors
    public Liberation() {
    }

    public Liberation(Week week, EmploiDuTemps emploiDuTemps) {
        this.week = week;
        this.emploiDuTemps = emploiDuTemps;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public EmploiDuTemps getEmploiDuTemps() {
        return emploiDuTemps;
    }

    public void setEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        this.emploiDuTemps = emploiDuTemps;
    }
}
