package com.school_1.api.Salle;

import com.school_1.api.Commons.Exceptions.DuplicationException;
import com.school_1.api.EmploiDuTemps.EmploiDuTempsEJB;
import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Liberation.LiberationEJB;
import com.school_1.api.Liberation.LiberationService;
import com.school_1.api.Reservation.ReservationEJB;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.Salle.models.CreateSallePayload;
import com.school_1.api.Salle.models.UpdateSallePayload;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class SalleService {
    @Inject
    private EmploiDuTempsEJB emploiDuTempsEJB;

    @Inject
    private ReservationEJB reservationEJB;

    @Inject
    private LiberationEJB liberationEJB;

    @Inject
    private SalleEJB salleEJB;

    @Inject
    private UserEJB userEJB;

    private void checkResponsableSallesRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() !=  UserRole.RESPONSABLE_SALLES) {
            throw new UnauthorizedException("Only RESPONSABLE_SALLES can perform this action");
        }
    }

    public Salle addSalle(CreateSallePayload payload, String email) throws UnauthorizedException, DuplicationException {
        checkResponsableSallesRole(email);
        if(salleEJB.findSalleByName(payload.getName()) != null) {
            throw new DuplicationException("Salle with name " + payload.getName() + " already exists");
        }
        Salle salle = payload.toSalle();
        return salleEJB.saveSalle(salle);
    }

    public Salle updateSalle(Long id, UpdateSallePayload payload, String email) throws NotFoundException, UnauthorizedException, DuplicationException {
        checkResponsableSallesRole(email);
        Salle existingSalle = salleEJB.findSalleById(id);
        if (existingSalle == null) {
            throw new NotFoundException("Salle not found");
        }

        if(salleEJB.findSalleByName(payload.getName()) != null && !salleEJB.findSalleByName(payload.getName()).getId().equals(id)) {
            throw new DuplicationException("Salle with name " + payload.getName() + " already exists");
        }

        existingSalle = payload.toSalle(existingSalle);
        return salleEJB.updateSalle(existingSalle);
    }

    public void deleteSalle(Long id, String email) throws NotFoundException, UnauthorizedException {
        checkResponsableSallesRole(email);
        Salle salle = salleEJB.findSalleById(id);
        if (salle == null) {
            throw new NotFoundException("Salle not found");
        }
        salleEJB.deleteSalle(id);
    }

    public Salle getSalleById(Long id) throws NotFoundException {
        Salle salle = salleEJB.findSalleById(id);
        if (salle == null) {
            throw new NotFoundException("Salle not found");
        }
        return salle;
    }

    public List<Salle> getAllSalles() {
        return salleEJB.findAllSalles();
    }

    public List<Salle> getFreeSallePerDayAndWeekAndSceance(Jour day, Week week, Seance seance) {
        return salleEJB.getFreeSallePerDayAndWeekAndSceance(day, week, seance);
    }
}