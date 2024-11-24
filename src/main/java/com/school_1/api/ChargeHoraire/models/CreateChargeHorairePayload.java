package com.school_1.api.ChargeHoraire.models;

import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Matiere.models.Matiere;
import jakarta.validation.constraints.NotNull;

public class CreateChargeHorairePayload {

    @NotNull(message = "Heures de cours ne peut pas être nulle")
    private Integer heuresCours;
    @NotNull(message = "Heures de TP ne peut pas être nulle")
    private Integer heuresTP;
    @NotNull(message = "Heures de TD ne peuvent pas être nulles")
    private Integer heuresTD;

    public Integer getHeuresTP() {
        return heuresTP;
    }

    public void setHeuresTP(Integer heuresTP) {
        this.heuresTP = heuresTP;
    }

    public Integer getHeuresCours() {
        return heuresCours;
    }

    public void setHeuresCours(Integer heuresCours) {
        this.heuresCours = heuresCours;
    }

    public Integer getHeuresTD() {
        return heuresTD;
    }

    public void setHeuresTD(Integer heuresTD) {
        this.heuresTD = heuresTD;
    }

    public ChargeHoraire toChargeHoraire(Filiere filiere, Matiere matiere) {
        return new ChargeHoraire(filiere, matiere, this.heuresCours, this.heuresTP, heuresTD);
    }
}
