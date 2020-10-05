package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * StateDao class provides the database access for all the required endpoints inside the address
 * controller
 */
@Repository
public class StateDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method helps to fetch the existing State by using StateUUID.
   *
   * @param stateUuid the state UUID which will be searched in database to find existing state.
   * @return StateEntity object if given state exists in database.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public StateEntity findStateByUUID(final String stateUuid) {
    try {
      StateEntity state = entityManager.createNamedQuery("stateByUUID", StateEntity.class)
          .setParameter("uuid", stateUuid).getSingleResult();
      return state;
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method fetch all states from database.
   *
   * @return List<StateEntity> object.
   */
  public List<StateEntity> getAllStates() {
    try {
      List<StateEntity> states = entityManager.createNamedQuery("getAllStates", StateEntity.class)
          .getResultList();
      return states;
    } catch (NoResultException nre) {
      return null;
    }
  }
}