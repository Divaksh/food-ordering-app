package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method gets lists of all restaurants
   *
   * @param
   * @return List of RestaurantEntity as per the restaurant ratings
   */
  public List<RestaurantEntity> getAllRestaurantsByRating() {
    List<RestaurantEntity> restaurantEntities = entityManager
        .createNamedQuery("getAllRestaurantsByRating", RestaurantEntity.class).getResultList();
    return restaurantEntities;
  }

  /**
   * Fetch the restaurant based on UUID.
   *
   * @param restaurantId
   * @return RestaurantEntity Restaurant details are returned as per the input parameter restaurant
   * uuid
   */
  public RestaurantEntity restaurantByUUID(String restaurantId) {
    try {
      return entityManager.createNamedQuery("restaurantByUUID", RestaurantEntity.class)
          .setParameter("uuid", restaurantId).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method updates restaurant
   *
   * @param restaurantEntity
   * @return List of restaurantEntity
   */
  public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity) {
    entityManager.merge(restaurantEntity);
    return restaurantEntity;
  }

}
