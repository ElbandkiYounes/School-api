package com.school_1.api.Liberation;

import com.school_1.api.Commons.Exceptions.BadRequestException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.EmploiDuTemps.EmploiDuTempsService;
import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.Liberation.models.CreateLiberationPayload;
import com.school_1.api.Liberation.models.Liberation;
import com.school_1.api.Reservation.ReservationEJB;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Stateless
public class LiberationService {

    @Inject
    private LiberationEJB liberationEJB;

    @Inject
    private EmploiDuTempsService emploiDuTempsService;

    @Inject
    private ReservationEJB reservationEJB;

    @Inject
    private UserEJB userEJB;

    private void checkProfessuerRole(String email) throws UnauthorizedException {
        User user = userEJB.findUserByEmail(email);
        if (user == null || user.getRole() != UserRole.PROFESSEUR) {
            throw new UnauthorizedException("Only users with role PROFESSEUR can perform this action");
        }
    }

    public Week getCurrentWeek() throws BadRequestException {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        // Définir le début de l'année scolaire (1er septembre)
        LocalDate startOfSchoolYear = LocalDate.of(
                today.getMonth().getValue() >= Month.SEPTEMBER.getValue() ? year : year - 1,
                9, 1
        );

        // Définir la fin de l'année scolaire (30 juin)
        LocalDate endOfSchoolYear = LocalDate.of(
                today.getMonth().getValue() <= Month.JUNE.getValue() ? year : year + 1,
                6, 30
        );

        // Vérifier si nous sommes dans l'année scolaire
        if (today.isBefore(startOfSchoolYear) || today.isAfter(endOfSchoolYear)) {
            throw new BadRequestException("Current date is outside the school year");
        }

        // Calculer le nombre de semaines depuis le début de l'année scolaire
        long weeksSinceStart = ChronoUnit.WEEKS.between(startOfSchoolYear, today) + 1;

        // Convertir en enum Week, en s'assurant de ne pas dépasser le nombre maximal de semaines
        int weekNumber = (int) Math.min(weeksSinceStart, Week.values().length);

        return Week.values()[weekNumber - 1];
    }

    public Jour getCurrentDay(){
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();

        // Convertissez DayOfWeek en votre enum Jour
        switch (currentDayOfWeek) {
            case MONDAY:
                return Jour.LUNDI;
            case TUESDAY:
                return Jour.MARDI;
            case WEDNESDAY:
                return Jour.MERCREDI;
            case THURSDAY:
                return Jour.JEUDI;
            case FRIDAY:
                return Jour.VENDREDI;
        }
        return null;
    }

    public Liberation addLiberation(CreateLiberationPayload payload, String email, Long emploiDuTempsId) throws BadRequestException, UnauthorizedException, NotFoundException {
        checkProfessuerRole(email);

        EmploiDuTemps emploiDuTemps = emploiDuTempsService.getEmploiDuTempsById(emploiDuTempsId);
        if (emploiDuTemps == null) {
            throw new NotFoundException("Emploi du temps not found");
        }

        if(!Objects.equals(emploiDuTemps.getUser().getEmail(), email)){
            throw new UnauthorizedException("You are not allowed to create a libiration on another professeur emploi du temps");
        }

        Week currentWeek = getCurrentWeek();
        Jour currentDay = getCurrentDay();

        if (payload.getWeek().ordinal() < currentWeek.ordinal()) {
            throw new BadRequestException("Cannot release for a past week");
        }

        // Si c'est la semaine courante, vérifiez aussi le jour
        if (payload.getWeek() == currentWeek) {
            if (currentDay == null || emploiDuTemps.getJour().ordinal() < currentDay.ordinal()) {
                throw new BadRequestException("Cannot release for a past day in the current week");
            }
        }

        Liberation liberation = payload.toLiberation(emploiDuTemps);
        return liberationEJB.createLiberation(liberation);
    }

    public void deleteLiberation(Long liberationId, String email) throws UnauthorizedException, NotFoundException {
        checkProfessuerRole(email);

        Liberation liberation = liberationEJB.getLiberationById(liberationId);
        if (liberation == null) {
            throw new NotFoundException("Liberation not found");
        }

        if(!Objects.equals(liberation.getEmploiDuTemps().getUser().getEmail(), email)){
            throw new UnauthorizedException("You are not allowed to delete a libiration on another professeur emploi du temps");
        }

        if(!reservationEJB.findReservationBySeanceANDSalleANDWeekANDJour(
                liberation.getEmploiDuTemps().getSeance(),
                liberation.getEmploiDuTemps().getSalle(),
                liberation.getWeek(),
                liberation.getEmploiDuTemps().getJour()).isEmpty()){
            throw new UnauthorizedException("You are not allowed to delete a libiration that has a reservation");
        }

        liberationEJB.DeleteLiberationById(liberation);
    }

    public List<Liberation> getAllLiberationsByProfesseurId(String email, Long Id) throws NotFoundException {
        if(userEJB.findUserById(Id) == null || userEJB.findUserById(Id).getRole() != UserRole.PROFESSEUR){
            throw new NotFoundException("Professeur not found");
        }
        return liberationEJB.getAllLiberationsByProfesseurId(Id);
    }

    public List<Liberation> getAllLiberations() {
        return liberationEJB.getAllLiberations();
    }

    public List<Liberation> getAllLiberationByWeek(Week week) {
        return liberationEJB.getAllLiberationsByWeek(week);
    }
}
