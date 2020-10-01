package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class StateDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public StateEntity findStateByUUID(final String uuid) {
        try {
            StateEntity state = entityManager.createNamedQuery("stateByUUID", StateEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
            return state;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<StateEntity> getAllStates() {
        try {
            List<StateEntity> states = entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();
            return states;
        } catch (NoResultException nre) {
            return null;
        }
    }
}