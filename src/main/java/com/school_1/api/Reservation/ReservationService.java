package com.school_1.api.Reservation;

import com.school_1.api.Commons.Exceptions.*;
import com.school_1.api.Commons.Services.EmailService;
import com.school_1.api.EmploiDuTemps.EmploiDuTempsEJB;
import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.Filiere.models.Filiere;
import com.school_1.api.Reservation.models.CreateReservationPayload;
import com.school_1.api.Reservation.models.Reservation;
import com.school_1.api.Reservation.models.ReservationStatus;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.SalleService;
import com.school_1.api.Filiere.FiliereService;
import com.school_1.api.Salle.models.Salle;
import com.school_1.api.User.UserEJB;
import com.school_1.api.User.UserService;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private UserEJB userEJB;

    @Inject
    private UserService userService;

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

    public Jour getCurrentDay() throws BadRequestException {
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
        if(reservationEJB.findResevationByProfesseurIdANDNotRegected(professeur.getId()) != null) {
            throw new DuplicationException("Professeur already has a reservation only one reservation is allowed");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDProfesseur(payload.getJour(), payload.getSeance(), professeur.getId()) != null) {
            throw new DuplicationException("Professeur already occupied");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourANDSeanceANDFiliere(payload.getJour(), payload.getSeance(), filiere.getId()) != null) {
            throw new DuplicationException("Filiere already occupied");
        }

        if(emploiDuTempsEJB.findEmploiDuTempsByJourAndSeanceANDSalle(payload.getJour(), payload.getSeance(), salle) != null) {
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

    @Schedule(hour = "23", minute = "59") // Run daily at 23:59
    public void cleanupPendingReservations() {
        LocalDate today = LocalDate.now();

        // Find all pending reservations
        List<Reservation> pendingReservations = reservationEJB.findPendingReservations();

        // Filter reservations that are past their reservation day
        List<Reservation> expiredPendingReservations = pendingReservations.stream()
                .filter(reservation ->
                        // Check if the reservation week and day are in the past
                        isReservationExpired(reservation, today)
                )
                .toList();

        // Update and notify
        for (Reservation reservation : expiredPendingReservations) {
            // Change status to REJECTED
            reservation.setReservationStatus(ReservationStatus.REJECTED);

            // Save the updated reservation
            reservationEJB.saveReservation(reservation);

            // Send notification email
            sendRejectionEmail(reservation);
        }
    }

    private boolean isReservationExpired(Reservation reservation, LocalDate currentDate) {
        // Get the start of the school year
        LocalDate startOfSchoolYear = LocalDate.of(
                currentDate.getMonth().getValue() >= Month.SEPTEMBER.getValue()
                        ? currentDate.getYear()
                        : currentDate.getYear() - 1,
                9, 1
        );

        // Calculate the exact date of the reservation based on week and day
        LocalDate reservationDate = calculateReservationDate(startOfSchoolYear, reservation);

        // Check if the reservation date is before or equal to the current date
        return reservationDate.isBefore(currentDate) || reservationDate.isEqual(currentDate);
    }

    private LocalDate calculateReservationDate(LocalDate startOfSchoolYear, Reservation reservation) {
        // Calculate the date of the reservation based on the week and day
        LocalDate reservationDate = startOfSchoolYear
                .plusWeeks(reservation.getWeek().ordinal())
                .plusDays(getJourOffset(reservation.getJour()));

        return reservationDate;
    }

    private int getJourOffset(Jour jour) {
        // Map Jour enum to the correct number of days to add
        switch (jour) {
            case LUNDI: return 0;
            case MARDI: return 1;
            case MERCREDI: return 2;
            case JEUDI: return 3;
            case VENDREDI: return 4;
            default: return 0;
        }
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

}
