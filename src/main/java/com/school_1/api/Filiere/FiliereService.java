package com.school_1.api.Filiere;

import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Filiere.models.CreateFilierePayload;
import com.school_1.api.Filiere.models.UpdateFilierePayload;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class FiliereService {

    @Inject
    private FiliereEJB filiereEJB;

    @Inject
    private UserEJB userEJB;

    private void checkCoordinatorRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() != UserRole.COORDINATEUR) {
            throw new UnauthorizedException("Only users with role COORDINATEUR can perform this action");
        }
    }

    public Filiere addFiliere(CreateFilierePayload payload, String email) throws UnauthorizedException {
        checkCoordinatorRole(email);
        Filiere filiere = payload.toFiliere();
        return filiereEJB.saveFiliere(filiere);
    }

    public Filiere updateFiliere(Long id, UpdateFilierePayload payload, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        Filiere existingFiliere = filiereEJB.findFiliereById(id);
        if (existingFiliere == null) {
            throw new NotFoundException("Filiere not found");
        }
        existingFiliere = payload.toFiliere(existingFiliere);
        return filiereEJB.updateFiliere(existingFiliere);
    }

    public void deleteFiliere(Long id, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        Filiere filiere = filiereEJB.findFiliereById(id);
        if (filiere == null) {
            throw new NotFoundException("Filiere not found");
        }
        filiereEJB.deleteFiliere(id);
    }

    public List<Filiere> getAllFilieres() {
        return filiereEJB.findAllFilieres();
    }

    public Filiere getFiliereById(Long id) throws NotFoundException {
        Filiere filiere = filiereEJB.findFiliereById(id);
        if (filiere == null) {
            throw new NotFoundException("Filiere not found");
        }
        return filiere;
    }
}