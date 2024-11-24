package com.school_1.api.Matiere;

import com.school_1.api.Matiere.models.Matiere;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
@LocalBean
public class MatiereEJB {
    @PersistenceContext
    private EntityManager entityManager;

    public Matiere saveMatiere(Matiere matiere) {
        entityManager.persist(matiere);
        return matiere;
    }

    public Matiere findMatiereById(Long id) {
        return entityManager.find(Matiere.class, id);
    }

    public Matiere findMatiereByName(String name) {
        try {
            return entityManager.createQuery("SELECT m FROM Matiere m WHERE m.name = :name", Matiere.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Matiere> findMatiereByKey(String key) {
        return entityManager.createQuery("SELECT m FROM Matiere m WHERE m.name LIKE :key", Matiere.class)
                .setParameter("key", "%" + key + "%")
                .getResultList();
    }

    public List<Matiere> findAllMatieres() {
        return entityManager.createQuery("SELECT m FROM Matiere m", Matiere.class).getResultList();
    }

    public Matiere updateMatiere(Matiere matiere) {
        return entityManager.merge(matiere);
    }

    public void deleteMatiere(Long id) {
        Matiere matiere = findMatiereById(id);
        if (matiere != null) {
            entityManager.remove(matiere);
        }
    }
}
