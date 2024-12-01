package com.school_1.api.EmploiDuTemps.models;

import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.Salle.models.SalleType;
import com.school_1.api.User.models.User;
import jakarta.validation.constraints.NotNull;

public class UpdateEmploiDuTempsPayload {

    @NotNull(message = "Jour cannot be null")
    private Jour jour;

    @NotNull(message = "Seance cannot be null")
    private Seance seance;

    @NotNull(message = "Type Seance cannot be null")
    private SalleType typeSeance;

    @NotNull(message = "Charge horaire ID cannot be null")
    private Long chargeHoraireId;

    @NotNull(message = "Salle ID cannot be null")
    private Long salleId;

    @NotNull(message = "Professeur ID cannot be null")
    private Long professeurId;

    // Getters and setters
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

    public SalleType getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(SalleType typeSeance) {
        this.typeSeance = typeSeance;
    }

    public Long getChargeHoraireId() {
        return chargeHoraireId;
    }

    public void setChargeHoraireId(Long chargeHoraireId) {
        this.chargeHoraireId = chargeHoraireId;
    }

    public Long getSalleId() {
        return salleId;
    }

    public void setSalleId(Long salleId) {
        this.salleId = salleId;
    }

    public Long getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(Long professeurId) {
        this.professeurId = professeurId;
    }

    public EmploiDuTemps toEmploiDuTemps(EmploiDuTemps existingEmploiDuTemps, ChargeHoraire chargeHoraire, Salle salle, User user) {
        existingEmploiDuTemps.setJour(this.jour);
        existingEmploiDuTemps.setSeance(this.seance);
        existingEmploiDuTemps.setTypeSeance(this.typeSeance);
        existingEmploiDuTemps.setChargeHoraire(chargeHoraire);
        existingEmploiDuTemps.setSalle(salle);
        existingEmploiDuTemps.setUser(user);
        return existingEmploiDuTemps;
    }
}