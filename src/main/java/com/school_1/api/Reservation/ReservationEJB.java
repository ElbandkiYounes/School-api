package com.school_1.api.Reservation;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Reservation.models.Reservation;
import com.school_1.api.Reservation.models.ReservationStatus;
import com.school_1.api.Reservation.models.Week;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@LocalBean
public class ReservationEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public Reservation saveReservation(Reservation reservation) {
        entityManager.persist(reservation);
        return reservation;
    }

    public void deleteReservationById(Long id) {
        Reservation reservation  = entityManager.find(Reservation.class, id);
        if(reservation != null) {
            entityManager.remove(reservation);
        }
    }

    public Reservation findReservationById(Long id) {
        return entityManager.find(Reservation.class, id);
    }

    public List<Reservation> findReservationsByProfesseurId(Long professeurId) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.professeur.id = :professeurId", Reservation.class)
                .setParameter("professeurId", professeurId)
                .getResultList();
    }

    public Reservation findResevationByProfesseurIdANDNotRegected(Long professeurId) {
        try{
            return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.professeur.id = :professeurId AND r.reservationStatus <> : reservationStatus", Reservation.class)
                .setParameter("professeurId", professeurId)
                    .setParameter("reservationStatus", ReservationStatus.REJECTED)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Reservation> findAllReservations() {
        return entityManager.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }

    public List<Reservation> findPendingReservations() {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.reservationStatus = :reservationStatus", Reservation.class)
                .setParameter("reservationStatus", ReservationStatus.PENDING)
                .getResultList();
    }

    public List<Reservation> findReservationsAcceptedBySalleAndSeanceANDJourANDWeek(Long salleId, Seance seance, Jour jour, Week week) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.salle.id = :salleId AND r.seance = :seance AND r.jour = :jour AND r.week = :week AND r.reservationStatus = : reservationStatus", Reservation.class)
                .setParameter("salleId", salleId)
                .setParameter("seance", seance)
                .setParameter("jour", jour)
                .setParameter("week", week)
                .setParameter("reservationStatus", ReservationStatus.ACCEPTED)
                .getResultList();
    }

    public List<Reservation> findReservationsAcceptedByFiliereAndSeanceAndJourAndWeek(Long filiereId,Seance seance, Jour jour, Week week) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.filiere.id = :filiereId AND r.jour = :jour AND r.week = :week AND r.seance = : seance AND r.reservationStatus = : reservationStatus", Reservation.class)
                .setParameter("filiereId", filiereId)
                .setParameter("jour", jour)
                .setParameter("week", week)
                .setParameter("seance", seance)
                .setParameter("reservationStatus", ReservationStatus.ACCEPTED)
                .getResultList();
    }

    // Method to find pending reservations by salle, seance, jour, and week
    public List<Reservation> findPendingReservationsBySalleAndSeanceAndJourAndWeek(Long salleId, Seance seance, Jour jour, Week week) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.salle.id = :salleId AND r.seance = :seance AND r.jour = :jour AND r.week = :week AND r.reservationStatus = :reservationStatus", Reservation.class)
                .setParameter("salleId", salleId)
                .setParameter("seance", seance)
                .setParameter("jour", jour)
                .setParameter("week", week)
                .setParameter("reservationStatus", ReservationStatus.PENDING)
                .getResultList();
    }

    // Method to find pending reservations by filiere, seance, jour, and week
    public List<Reservation> findPendingReservationsByFiliereAndSeanceAndJourAndWeek(Long filiereId, Seance seance, Jour jour, Week week) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.filiere.id = :filiereId AND r.seance = :seance AND r.jour = :jour AND r.week = :week AND r.reservationStatus = :reservationStatus", Reservation.class)
                .setParameter("filiereId", filiereId)
                .setParameter("seance", seance)
                .setParameter("jour", jour)
                .setParameter("week", week)
                .setParameter("reservationStatus", ReservationStatus.PENDING)
                .getResultList();
    }

}
