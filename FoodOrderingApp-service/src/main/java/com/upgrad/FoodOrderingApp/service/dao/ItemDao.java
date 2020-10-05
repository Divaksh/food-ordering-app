package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Fetch the item based on id.
   *
   * @param itemId UUID of the item to be fetched.
   * @return ItemEntity if found in database else null
   */
  public ItemEntity getItemById(String itemId) {
    try {
      ItemEntity itemEntity = entityManager.createNamedQuery("itemById", ItemEntity.class)
          .setParameter("itemId", itemId)
          .getSingleResult();
      return itemEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Fetch the item based on UUID.
   *
   * @param itemUUID UUID of the item to be fetched.
   * @return ItemEntity if found in database else null
   */
  public ItemEntity getItemByUUID(String itemUUID) {
    try {
      return entityManager.createNamedQuery("itemByUUID", ItemEntity.class)
          .setParameter("uuid", itemUUID).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
