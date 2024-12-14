package com.school_1.api.Reservation;

import com.school_1.api.Commons.Exceptions.*;
import com.school_1.api.Commons.Services.EmailService;
import com.school_1.api.EmploiDuTemps.EmploiDuTempsEJB;
import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Liberation.LiberationEJB;
import com.school_1.api.Reservation.models.CreateReservationPayload;
import com.school_1.api.Reservation.models.Reservation;
import com.school_1.api.Reservation.models.ReservationStatus;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.SalleService;
import com.school_1.api.Filiere.FiliereService;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Stateless
public class ReservationService {

    @Inject
    private ReservationEJB reservationEJB;

    @Inject
    private EmploiDuTempsEJB emploiDuTempsEJB;

    @Inject
    private SalleService salleService;

    @Inject
    private FiliereService filiereService;

    @Inject
    private LiberationEJB liberationEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    private EmailService emailService;


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

    public Jour getCurrentDay() {
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

    public void sendEmailAcceptedReservation(Reservation reservation) {
        String subject = "Reservation Accepted";
        String body = "Your reservation has been accepted";
        emailService.sendEmail(reservation.getProfesseur().getEmail(), subject, body);
    }

    public List<Reservation> getAllReservations() {
        return reservationEJB.findAllReservations();
    }

    public List<Reservation> getPronfesseurReservations(Long id) throws NotFoundException {
        if(userEJB.findUserById(id) == null){
            throw new NotFoundException("Professeur not found");
        }
        return reservationEJB.findReservationsByProfesseurId(id);
    }

    public Reservation addReservation(CreateReservationPayload payload, String email)
            throws DuplicationException, UnauthorizedException, NotFoundException, BadRequestException {

        checkProfessuerRole(email);

        // Récupérez la semaine et le jour courants
        Week currentWeek = getCurrentWeek();
        Jour currentDay = getCurrentDay();

        // Vérifiez si la semaine de la réservation est valide
        if (payload.getWeek().ordinal() < currentWeek.ordinal()) {
            throw new BadRequestException("Cannot reserve for a past week");
        }

        // Si c'est la semaine courante, vérifiez aussi le jour
        if (payload.getWeek() == currentWeek) {
            if (currentDay == null || payload.getJour().ordinal() < currentDay.ordinal()) {
                throw new BadRequestException("Cannot reserve for a past day in the current week");
            }
        }

        User professeur = userEJB.findUserByEmail(email);
        Filiere filiere = filiereService.getFiliereById(payload.getFiliereId());
        Salle salle = salleService.getSalleById(payload.getSalleId());

        // Autres vérifications de disponibilité
        if(reservationEJB.findReservationByProfesseurIdANDSenaceANDDayANDWeekANDSalleNotRejectedOrPassed(professeur.getId(), payload.getSeance(), payload.getJour(), payload.getWeek(), salle) != null) {
            throw new DuplicationException("Professeur already has a pending or accepted reservation in the same timestamp and salle");
        }
        EmploiDuTemps emp = emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDProfesseur(payload.getJour(), payload.getSeance(), professeur.getId());
        if(emp != null && (liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()) == null || liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()).getWeek().ordinal() != payload.getWeek().ordinal())) {
            throw new DuplicationException("Professeur already occupied");
        }
        emp = emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), filiere.getId());
        if(emp != null && (liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()) == null || liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()).getWeek().ordinal() != payload.getWeek().ordinal())) {
            throw new DuplicationException("Filiere already occupied");
        }

        emp = emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDSalle(payload.getJour(), payload.getSeance(), salle);
        if(emp!= null && (liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()) == null || liberationEJB.getLiberationByEmploiDuTempsId(emp.getId()).getWeek().ordinal() != payload.getWeek().ordinal())) {
            throw new DuplicationException("Salle already occupied");
        }

        // Logique de réservation finale
        if(reservationEJB.findReservationsAcceptedBySalleAndSeanceANDJourANDWeek(payload.getSalleId(), payload.getSeance(), payload.getJour(), payload.getWeek()).isEmpty() &&
                reservationEJB.findReservationsAcceptedByFiliereAndSeanceAndJourAndWeek(payload.getFiliereId(), payload.getSeance(), payload.getJour(), payload.getWeek()).isEmpty()) {
            Reservation reservation = payload.toReservation(ReservationStatus.ACCEPTED, professeur, salle, filiere);
            return reservationEJB.saveReservation(reservation);
        } else {
            Reservation reservation = payload.toReservation(ReservationStatus.PENDING, professeur, salle, filiere);
            return reservationEJB.saveReservation(reservation);
        }
    }

    public void deleteReservation(Long id, String email) throws UnauthorizedException, NotFoundException, AccessDeniedException {
        checkProfessuerRole(email);
        Reservation reservation = reservationEJB.findReservationById(id);
        if (reservation == null) {
            throw new NotFoundException("Reservation not found");
        }

        if(!Objects.equals(reservation.getProfesseur().getEmail(), email)) {
            throw new AccessDeniedException("You are not allowed to delete this reservation");
        }

        reservationEJB.deleteReservationById(id);


        // Check for pending reservations and update their status to accepted in FIFO order
        List<Reservation> pendingReservationsCausedBySalle = reservationEJB.findPendingReservationsBySalleAndSeanceAndJourAndWeek(
                reservation.getSalle().getId(), reservation.getSeance(), reservation.getJour(), reservation.getWeek());
        if (!pendingReservationsCausedBySalle.isEmpty()) {
            pendingReservationsCausedBySalle.sort(Comparator.comparing(Reservation::getId));
            Reservation pendingReservation = pendingReservationsCausedBySalle.get(0);
            pendingReservation.setReservationStatus(ReservationStatus.ACCEPTED);
            reservationEJB.saveReservation(pendingReservation);
            sendEmailAcceptedReservation(pendingReservation);
        }

        List<Reservation> pendingReservationsCausedByFiliere = reservationEJB.findPendingReservationsByFiliereAndSeanceAndJourAndWeek(
                reservation.getFiliere().getId(), reservation.getSeance(), reservation.getJour(), reservation.getWeek());
        if (!pendingReservationsCausedByFiliere.isEmpty()) {
            pendingReservationsCausedByFiliere.sort(Comparator.comparing(Reservation::getId));
            Reservation pendingReservation = pendingReservationsCausedByFiliere.get(0);
            pendingReservation.setReservationStatus(ReservationStatus.ACCEPTED);
            reservationEJB.saveReservation(pendingReservation);
            sendEmailAcceptedReservation(pendingReservation);
        }
    }

    @Schedule(hour = "10", minute = "1") // After SEANCE_1 (8-10)
    @Schedule(hour = "12", minute = "1") // After SEANCE_2 (10-12)
    @Schedule(hour = "16", minute = "1") // After SEANCE_3 (14-16)
    @Schedule(hour = "18", minute = "1") // After SEANCE_4 (16-18)
    public void cleanupReservations() throws BadRequestException {
        LocalDate today = LocalDate.now();
        Seance currentSeance = determineCurrentSeance();

        if (currentSeance == null) {
            return; // Exit if no valid seance
        }

        // Find all pending and accepted reservations
        List<Reservation> reservationsToCleanup = reservationEJB.findReservationsBySeanceAndStatus(
                currentSeance,
                List.of(ReservationStatus.PENDING, ReservationStatus.ACCEPTED)
        );

        for (Reservation reservation : reservationsToCleanup) {
            // Check if the reservation is for the current seance and has passed
            if (isReservationExpired(reservation, today, currentSeance)) {
                // For pending reservations, change to REJECTED
                if (reservation.getReservationStatus() == ReservationStatus.PENDING) {
                    reservation.setReservationStatus(ReservationStatus.REJECTED);
                    sendRejectionEmail(reservation);
                }
                // For accepted reservations, change to PASSED
                else if (reservation.getReservationStatus() == ReservationStatus.ACCEPTED) {
                    reservation.setReservationStatus(ReservationStatus.PASSED);
                    sendPassedEmail(reservation);
                }

                // Save the updated reservation
                reservationEJB.saveReservation(reservation);
            }
        }
    }

    private Seance determineCurrentSeance() {
        LocalTime now = LocalTime.now();

        if (now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(10, 0))) {
            return Seance.SEANCE_1;
        } else if (now.isAfter(LocalTime.of(10, 0)) && now.isBefore(LocalTime.of(12, 0))) {
            return Seance.SEANCE_2;
        } else if (now.isAfter(LocalTime.of(14, 0)) && now.isBefore(LocalTime.of(16, 0))) {
            return Seance.SEANCE_3;
        } else if (now.isAfter(LocalTime.of(16, 0)) && now.isBefore(LocalTime.of(18, 0))) {
            return Seance.SEANCE_4;
        }

        return null;
    }

    private boolean isReservationExpired(Reservation reservation, LocalDate currentDate, Seance currentSeance) throws BadRequestException {
        // Check if the reservation is for the current week and day
        Week currentWeek = getCurrentWeek();
        Jour currentDay = getCurrentDay();

        return (reservation.getWeek() == currentWeek &&
                reservation.getJour() == currentDay &&
                reservation.getSeance() == currentSeance);
    }

    private void sendRejectionEmail(Reservation reservation) {
        String subject = "Reservation Expired";
        String body = "Your reservation has been automatically rejected as it was not processed in time.";

        emailService.sendEmail(
                reservation.getProfesseur().getEmail(),
                subject,
                body
        );
    }

    private void sendPassedEmail(Reservation reservation) {
        String subject = "Reservation Passed";
        String body = "Your reservation has passed";

        emailService.sendEmail(
                reservation.getProfesseur().getEmail(),
                subject,
                body
        );
    }

}
