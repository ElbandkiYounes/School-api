package com.school_1.api.Matiere;

import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Matiere.models.CreateMatierePayload;
import com.school_1.api.Matiere.models.Matiere;
import com.school_1.api.Matiere.models.UpdateMatierePayload;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class MatiereService {

    @Inject
    private MatiereEJB matiereEJB;

    @Inject
    private UserEJB userEJB;

    private void checkCoordinatorRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() != UserRole.COORDINATEUR) {
            throw new UnauthorizedException("Only users with role COORDINATEUR can perform this action");
        }
    }

    public Matiere addMatiere(CreateMatierePayload createMatierePayload, String email) throws UnauthorizedException, DuplicationException {
        checkCoordinatorRole(email);
        Matiere matiere = matiereEJB.findMatiereByName(createMatierePayload.getName());
        if (matiere != null) {
            throw new DuplicationException("Matiere with this name already exists");
        }
        matiere = createMatierePayload.toMatiere();
        return matiereEJB.saveMatiere(matiere);
    }

    public Matiere updateMatiere(Long id, UpdateMatierePayload updateMatierePayload, String email) throws UnauthorizedException, NotFoundException, DuplicationException {
        checkCoordinatorRole(email);
        Matiere existingMatiere = matiereEJB.findMatiereById(id);
        if (existingMatiere == null) {
            throw new NotFoundException("Matiere not found");
        }
        if(matiereEJB.findMatiereByName(updateMatierePayload.getName()) != null) {
            throw new DuplicationException("Matiere with this new name already exists");
        }
        existingMatiere = updateMatierePayload.toMatiere(existingMatiere);
        return matiereEJB.updateMatiere(existingMatiere);
    }

    public void deleteMatiere(Long id, String email) throws UnauthorizedException, NotFoundException {
        checkCoordinatorRole(email);
        Matiere matiere = matiereEJB.findMatiereById(id);
        if (matiere == null) {
            throw new NotFoundException("Matiere not found");
        }
        matiereEJB.deleteMatiere(id);
    }

    public List<Matiere> getAllMatieres() {
        return matiereEJB.findAllMatieres();
    }

    public Matiere getMatiereById(Long id) throws NotFoundException {
        Matiere matiere = matiereEJB.findMatiereById(id);
        if (matiere == null) {
            throw new NotFoundException("Matiere not found");
        }
        return matiere;
    }

    public List<Matiere> getMatieresByKey(String key) {
        return matiereEJB.findMatiereByKey(key);
    }
}