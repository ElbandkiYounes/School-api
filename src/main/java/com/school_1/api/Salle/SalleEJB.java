package com.school_1.api.Salle;

import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Reservation.models.Week;
import com.school_1.api.Salle.models.Salle;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
@LocalBean
public class SalleEJB {

    @PersistenceContext
    private EntityManager entityManager;

    public Salle saveSalle(Salle salle) {
        entityManager.persist(salle);
        return salle;
    }

    public Salle updateSalle(Salle salle) {
        return entityManager.merge(salle);
    }

    public void deleteSalle(Long id) {
        Salle salle = findSalleById(id);
        if (salle != null) {
            entityManager.remove(salle);
        }
    }

    public Salle findSalleById(Long id) {
        return entityManager.find(Salle.class, id);
    }

    public List<Salle> findAllSalles() {
        TypedQuery<Salle> query = entityManager.createQuery("SELECT s FROM Salle s", Salle.class);
        return query.getResultList();
    }

    public Salle findSalleByName(String name) {
        try{
            return entityManager.createQuery("SELECT s FROM Salle s WHERE s.name = :name", Salle.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }catch (NoResultException e) {
            return null;
        }
    }

    public List<Salle> getFreeSallePerDayAndWeekAndSceance(Jour day, Week week, Seance seance) {
        return entityManager.createQuery(
                        "SELECT s FROM Salle s WHERE s.id NOT IN (" +
                                "SELECT r.salle.id FROM Reservation r WHERE r.jour = :day AND r.week = :week AND r.seance = :seance" +
                                ") AND (" +
                                "s.id IN (" +
                                "SELECT l.emploiDuTemps.salle.id FROM Liberation l WHERE l.emploiDuTemps.jour = :day AND l.week = :week AND l.emploiDuTemps.seance = :seance" +
                                ") OR s.id NOT IN (" +
                                "SELECT e.salle.id FROM EmploiDuTemps e WHERE e.jour = :day AND e.seance = :seance" +
                                "))", Salle.class)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("seance", seance)
                .getResultList();
    }
}