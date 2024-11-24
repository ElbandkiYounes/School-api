package com.school_1.api.Filiere;

import com.school_1.api.Filiere.models.Filiere;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
@LocalBean
public class FiliereEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public Filiere saveFiliere(Filiere filiere) {
        entityManager.persist(filiere);
        return filiere;
    }

    public Filiere findFiliereById(Long id) {
        return entityManager.find(Filiere.class, id);
    }

    public List<Filiere> findAllFilieres() {
        return entityManager.createQuery("SELECT f FROM Filiere f", Filiere.class).getResultList();
    }

    public Filiere updateFiliere(Filiere filiere) {
        return entityManager.merge(filiere);
    }

    public void deleteFiliere(Long id) {
        Filiere filiere = findFiliereById(id);
        if (filiere != null) {
            entityManager.remove(filiere);
        }
    }
}
