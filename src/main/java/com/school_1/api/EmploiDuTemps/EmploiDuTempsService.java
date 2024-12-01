package com.school_1.api.EmploiDuTemps;

import com.school_1.api.ChargeHoraire.ChargeHoraireService;
import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import com.school_1.api.Commons.Exceptions.BadRequestException;
import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.EmploiDuTemps.models.*;
import com.school_1.api.Filiere.FiliereService;
import com.school_1.api.Salle.SalleService;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;

@Stateless
public class EmploiDuTempsService {

    @Inject
    private EmploiDuTempsEJB emploiDuTempsEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    private SalleService salleService;

    @Inject
    private FiliereService filiereService;

    @Inject
    private ChargeHoraireService chargeHoraireService;


    private void checkCoordinatorRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() != UserRole.COORDINATEUR) {
            throw new UnauthorizedException("Only users with role COORDINATEUR can perform this action");
        }
    }

    private void checkSalleAvailabilityAdd(Salle salle, Jour jour, Seance seance) throws DuplicationException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDSalle(jour, seance, salle);
        if (emploiDuTemps != null) {
            throw new DuplicationException("Salle is already occupied");
        }
    }

    private void checkSalleAvailabilityUpdate(Salle salle, Jour jour, Seance seance, Long id) throws DuplicationException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDSalle(jour, seance, salle);
        if (emploiDuTemps != null && !Objects.equals(emploiDuTemps.getId(), id)) {
            throw new DuplicationException("Salle is already occupied");
        }
    }

    public EmploiDuTemps addEmploiDuTemps(CreateEmploiDuTempsPayload payload, String email, Long filiereId) throws UnauthorizedException, NotFoundException, DuplicationException, BadRequestException {
        checkCoordinatorRole(email);
        ChargeHoraire chargeHoraire = chargeHoraireService.getChargeHoraireById(payload.getChargeHoraireId());

        if(!Objects.equals(chargeHoraire.getFiliere().getId(), filiereId)) {
            throw new NotFoundException("ChargeHoraire Not found");
        }

        Salle salle = salleService.getSalleById(payload.getSalleId());
        if(!salle.getType().name().equals(payload.getTypeSeance().name())) {
            throw new BadRequestException("Incompatible salle type");
        }

        User professeur = userEJB.findUserById(payload.getProfesseurId());
        if( professeur == null || professeur.getRole() != UserRole.PROFESSEUR) {
            throw new NotFoundException("Professeur Not found");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), filiereId) != null) {
            throw new DuplicationException("Filiere is already occupied");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDProfesseur(payload.getJour(), payload.getSeance(), professeur.getId()) != null) {
            throw new DuplicationException("Professeur is already occupied");
        }

        checkSalleAvailabilityAdd(salle, payload.getJour(), payload.getSeance());
        EmploiDuTemps emploiDuTemps = payload.toEmploiDuTemps(chargeHoraire, salle, professeur);
        return  emploiDuTempsEJB.saveEmploiDuTemps(emploiDuTemps);
    }

    public EmploiDuTemps updateEmploiDuTemps(Long id, UpdateEmploiDuTempsPayload payload, String email) throws UnauthorizedException, NotFoundException, DuplicationException, BadRequestException {
        checkCoordinatorRole(email);
        EmploiDuTemps emploiDuTemps = getEmploiDuTempsById(id);
        ChargeHoraire chargeHoraire = chargeHoraireService.getChargeHoraireById(payload.getChargeHoraireId());

        if(!Objects.equals(chargeHoraire.getFiliere().getId(), emploiDuTemps.getChargeHoraire().getFiliere().getId())) {
            throw new NotFoundException("ChargeHoraire Not found");
        }

        Salle salle = salleService.getSalleById(payload.getSalleId());
        if(!salle.getType().name().equals(payload.getTypeSeance().name())) {
            throw new BadRequestException("Incompatible salle type");
        }

        User professeur = userEJB.findUserById(payload.getProfesseurId());
        if( professeur == null || professeur.getRole() != UserRole.PROFESSEUR) {
            throw new NotFoundException("Professeur Not found");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), chargeHoraire.getFiliere().getId()) != null && !Objects.equals(emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), chargeHoraire.getFiliere().getId()).getId(), id)) {
            throw new DuplicationException("Filiere is already occupied");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDProfesseur(payload.getJour(), payload.getSeance(), professeur.getId()) != null && !Objects.equals(emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), chargeHoraire.getFiliere().getId()).getId(), id)) {
            throw new DuplicationException("Professeur is already occupied");
        }
        checkSalleAvailabilityUpdate(salle, payload.getJour(), payload.getSeance(), id);
        EmploiDuTemps existingEmploiDuTemps = getEmploiDuTempsById(id);
        existingEmploiDuTemps = payload.toEmploiDuTemps(existingEmploiDuTemps, chargeHoraire, salle, professeur);
        return emploiDuTempsEJB.updateEmploiDuTemps(existingEmploiDuTemps);
    }

    public EmploiDuTemps getEmploiDuTempsById(Long id) throws NotFoundException {
        EmploiDuTemps emploiDuTemps = emploiDuTempsEJB.findEmploiDuTempsById(id);
        if (emploiDuTemps == null) {
            throw new NotFoundException("EmploiDuTemps not found");
        }
        return emploiDuTemps;
    }

    public List<EmploiDuTemps> getEmploisDuTempsByFiliereId(Long filiereId) throws NotFoundException {
        filiereService.getFiliereById(filiereId);
        return emploiDuTempsEJB.findEmploisDuTempsByFiliereId(filiereId);
    }

    public List<EmploiDuTemps> getEmploisDuTempsByProfesseurId(Long professeurId) throws NotFoundException {
        if(userEJB.findUserById(professeurId) == null || userEJB.findUserById(professeurId).getRole() != UserRole.PROFESSEUR) {
            throw new NotFoundException("Professeur not found");
        }
        return emploiDuTempsEJB.findEmploisDuTempsByProfesseurId(professeurId);
    }

    public void deleteEmploiDuTemps(Long id, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        EmploiDuTemps emploiDuTemps = emploiDuTempsEJB.findEmploiDuTempsById(id);
        if (emploiDuTemps == null) {
            throw new NotFoundException("EmploiDuTemps not found");
        }
        emploiDuTempsEJB.deleteEmploiDuTemps(id);
    }

    public void deleteEmploiDuTempsByFiliereId(Long filiereId, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        filiereService.getFiliereById(filiereId);
        emploiDuTempsEJB.deleteEmploiDuTempsByFiliereId(filiereId);
    }


}
