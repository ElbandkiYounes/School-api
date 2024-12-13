package com.school_1.api.Salle.models;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Reservation.models.Week;
import jakarta.validation.constraints.NotNull;

public class GetPayload {
    @NotNull(message = "Day is required")
    private Jour day;

    @NotNull(message = "Week is required")
    private Week week;

    @NotNull(message = "Seance is required")
    private Seance seance;

    public Jour getDay() {
        return day;
    }

    public void setDay(Jour day) {
        this.day = day;
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
}
