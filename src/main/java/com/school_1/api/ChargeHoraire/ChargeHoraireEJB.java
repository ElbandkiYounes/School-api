package com.school_1.api.ChargeHoraire;

import com.school_1.api.ChargeHoraire.models.ChargeHoraire;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@LocalBean
public class ChargeHoraireEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public ChargeHoraire saveChargeHoraire(ChargeHoraire chargeHoraire) {
        entityManager.persist(chargeHoraire);
        return chargeHoraire;
    }

    public List<ChargeHoraire> findChargesHorairesByFiliereId(Long filiereId) {
        return entityManager.createQuery("SELECT c FROM ChargeHoraire c WHERE c.filiere.id = :filiereId", ChargeHoraire.class)
                .setParameter("filiereId", filiereId)
                .getResultList();
    }

    public ChargeHoraire findChargeHoraireByFiliereIdAndMatiereId(Long filiereId, Long matiereId) {
        try {
            return entityManager.createQuery("SELECT c FROM ChargeHoraire c WHERE c.filiere.id = :filiereId AND c.matiere.id = :matiereId", ChargeHoraire.class)
                    .setParameter("filiereId", filiereId)
                    .setParameter("matiereId", matiereId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ChargeHoraire findChargeHoraireById(Long id) {
        return entityManager.find(ChargeHoraire.class, id);
    }

    public ChargeHoraire updateChargeHoraire(ChargeHoraire chargeHoraire) {
        return entityManager.merge(chargeHoraire);
    }

    public void deleteChargeHoraire(Long id) {
        ChargeHoraire chargeHoraire = findChargeHoraireById(id);
        if (chargeHoraire != null) {
            entityManager.remove(chargeHoraire);
        }
    }

    public void deleteChargeHoraireByFiliereId(Long filiereId) {
        List<ChargeHoraire> chargeHoraires = findChargesHorairesByFiliereId(filiereId);
        chargeHoraires.forEach(chargeHoraire -> entityManager.remove(chargeHoraire));
    }

}
