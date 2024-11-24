package com.school_1.api.ChargeHoraire;

import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import com.school_1.api.ChargeHoraire.models.CreateChargeHorairePayload;
import com.school_1.api.ChargeHoraire.models.UpdateChargeHorairePayload;
import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Filiere.FiliereService;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Matiere.MatiereService;
import com.school_1.api.Matiere.models.Matiere;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class ChargeHoraireService {

    @Inject
    private ChargeHoraireEJB chargeHoraireEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    private FiliereService filiereService;

    @Inject
    private MatiereService matiereService;

    private void checkCoordinatorRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() != UserRole.COORDINATEUR) {
            throw new UnauthorizedException("Only users with role COORDINATEUR can perform this action");
        }
    }

    public ChargeHoraire addChargeHoraire(CreateChargeHorairePayload payload, String email, Long filiereId, Long matiereId) throws UnauthorizedException, NotFoundException, DuplicationException {
        checkCoordinatorRole(email);
        if(chargeHoraireEJB.findChargeHoraireByFiliereIdAndMatiereId(filiereId, matiereId) != null) {
            throw new DuplicationException("ChargeHoraire de la matiere avec id:"+matiereId+" deja definie pour la filiere id:"+filiereId+" already exists");
        }
        Filiere filiere = filiereService.getFiliereById(filiereId);
        Matiere matiere = matiereService.getMatiereById(matiereId);

        ChargeHoraire chargeHoraire = payload.toChargeHoraire(filiere, matiere);
        return chargeHoraireEJB.saveChargeHoraire(chargeHoraire);
    }

    public ChargeHoraire updateChargeHoraire(Long id, UpdateChargeHorairePayload payload, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        ChargeHoraire existingChargeHoraire = chargeHoraireEJB.findChargeHoraireById(id);
        if (existingChargeHoraire == null) {
            throw new NotFoundException("ChargeHoraire not found");
        }
        existingChargeHoraire = payload.toChargeHoraire(existingChargeHoraire);
        return chargeHoraireEJB.updateChargeHoraire(existingChargeHoraire);
    }

    public void deleteChargeHoraire(Long id, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        ChargeHoraire chargeHoraire = chargeHoraireEJB.findChargeHoraireById(id);
        if (chargeHoraire == null) {
            throw new NotFoundException("ChargeHoraire not found");
        }
        chargeHoraireEJB.deleteChargeHoraire(id);
    }

    public ChargeHoraire getChargeHoraireById(Long id) throws NotFoundException {
        ChargeHoraire chargeHoraire = chargeHoraireEJB.findChargeHoraireById(id);
        if (chargeHoraire == null) {
            throw new NotFoundException("ChargeHoraire not found");
        }
        return chargeHoraire;
    }

    public List<ChargeHoraire> getChargesHorairesByFiliere(Long filiereId) throws NotFoundException {
        Filiere filiere = filiereService.getFiliereById(filiereId);
        if (filiere == null) {
            throw new NotFoundException("Filiere not found");
        }
        return chargeHoraireEJB.findChargesHorairesByFiliereId(filiereId);
    }

}
