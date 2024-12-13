package com.school_1.api.Liberation;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Liberation.models.Liberation;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.models.Salle;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@LocalBean
public class LiberationEJB {

    @PersistenceContext
    private EntityManager em;

    public Liberation getLiberationById(Long id) {
        return em.find(Liberation.class, id);
    }

    public Liberation getLiberationByEmploiDuTempsId(Long id) {
        try {
            return em.createQuery("SELECT l FROM Liberation l WHERE l.emploiDuTemps.id = :emploiDuTempsId", Liberation.class)
                    .setParameter("emploiDuTempsId", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    public List<Liberation> getAllLiberations() {
        return em.createQuery("SELECT l FROM Liberation l", Liberation.class)
                .getResultList();
    }

    public List<Liberation> getAllLiberationsByWeek(Week week) {
        return em.createQuery("SELECT l FROM Liberation l WHERE l.week = :week", Liberation.class)
                .setParameter("week", week)
                .getResultList();
    }

    public List<Liberation> getAllLiberationsByWeekANDBySalleANDBySeanceANDByJour(Week week, Salle salle, Seance seance, Jour jour) {
        return em.createQuery("SELECT l FROM Liberation l WHERE l.week = :week AND l.emploiDuTemps.salle = :salle AND l.emploiDuTemps.seance = :seance AND l.emploiDuTemps.jour = :jour", Liberation.class)
                .setParameter("week", week)
                .setParameter("salle", salle)
                .setParameter("seance", seance)
                .setParameter("jour", jour)
                .getResultList();
    }

    public List<Liberation> getAllLiberationsByProfesseurId(Long id) {
        return em.createQuery("SELECT l FROM Liberation l WHERE l.emploiDuTemps.professeur.id = :professeurId", Liberation.class)
                .setParameter("professeurId", id)
                .getResultList();
    }

    public Liberation createLiberation(Liberation liberation) {
        em.persist(liberation);
        return liberation;
    }

    public void DeleteLiberationById(Liberation liberation) {
        em.remove(liberation);
    }
}
