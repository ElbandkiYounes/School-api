package com.school_1.api.EmploiDuTemps;

import com.school_1.api.EmploiDuTemps.models.EmploiDuTemps;
import com.school_1.api.EmploiDuTemps.models.Jour;
import com.school_1.api.EmploiDuTemps.models.Seance;
import com.school_1.api.Salle.models.Salle;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@LocalBean
public class EmploiDuTempsEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public EmploiDuTemps saveEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        entityManager.persist(emploiDuTemps);
        return emploiDuTemps;
    }

    public EmploiDuTemps updateEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        return entityManager.merge(emploiDuTemps);
    }

    public List<EmploiDuTemps> findEmploisDuTempsByFiliereId(Long filiereId) {
        return entityManager.createQuery("SELECT e FROM EmploiDuTemps e WHERE e.chargeHoraire.filiere.id = :filiereId", EmploiDuTemps.class)
                .setParameter("filiereId", filiereId)
                .getResultList();
    }

    public EmploiDuTemps findEmploiDuTempsById(Long id) {
        return entityManager.find(EmploiDuTemps.class, id);
    }

    public EmploiDuTemps findEmploiDuTempsByJourAndSeanceANDSalle(Jour jour, Seance seance, Salle salle) {
        try{
            return entityManager.createQuery("SELECT e FROM EmploiDuTemps e WHERE e.jour = :jour AND e.seance = :seance AND e.salle = :salle", EmploiDuTemps.class)
                    .setParameter("jour", jour)
                    .setParameter("seance", seance)
                    .setParameter("salle", salle)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public EmploiDuTemps findEmploiDuTempsByJourANDSeanceANDFiliere(Jour jour, Seance seance, Long filiereId) {
        try{
            return entityManager.createQuery("SELECT e FROM EmploiDuTemps e WHERE e.jour = :jour AND e.seance = :seance AND e.chargeHoraire.filiere.id = :filiereId", EmploiDuTemps.class)
                    .setParameter("jour", jour)
                    .setParameter("seance", seance)
                    .setParameter("filiereId", filiereId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<EmploiDuTemps> findEmploisDuTempsByProfesseurId(Long professeurId) {
        try{
            return entityManager.createQuery("SELECT e FROM EmploiDuTemps e WHERE e.professeur.id = :professeurId", EmploiDuTemps.class)
                    .setParameter("professeurId", professeurId)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public EmploiDuTemps findEmploiDuTempsByJourAndSeanceANDProfesseur(Jour jour, Seance seance, Long professeurId) {
        try{
            return entityManager.createQuery("SELECT e FROM EmploiDuTemps e WHERE e.jour = :jour AND e.seance = :seance AND e.professeur.id = :professeurId", EmploiDuTemps.class)
                    .setParameter("jour", jour)
                    .setParameter("seance", seance)
                    .setParameter("professeurId", professeurId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteEmploiDuTempsByFiliereId(Long filiereId) {
        List<EmploiDuTemps> emploisDuTemps = findEmploisDuTempsByFiliereId(filiereId);
        emploisDuTemps.forEach(emploiDuTemps -> entityManager.remove(emploiDuTemps));
    }

    public void deleteEmploiDuTemps(Long id) {
        EmploiDuTemps emploiDuTemps = findEmploiDuTempsById(id);
        if (emploiDuTemps != null) {
            entityManager.remove(emploiDuTemps);
        }
    }
}
