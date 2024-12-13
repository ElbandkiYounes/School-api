package com.school_1.api.Liberation.models;

import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.Reservation.models.Week;
import jakarta.validation.constraints.NotNull;

public class CreateLiberationPayload {

    @NotNull(message = "Reservation Week cannot be null")
    private Week week;

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Liberation toLiberation(EmploiDuTemps emploiDuTemps) {
        return new Liberation(week, emploiDuTemps);
    }
}
