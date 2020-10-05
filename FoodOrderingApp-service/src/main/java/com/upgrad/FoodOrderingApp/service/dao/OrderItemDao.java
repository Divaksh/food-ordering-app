package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Fetch all the item of a given order.
   *
   * @param orderEntity
   * @return List of OrderEntity.
   */
  public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
    try {
      return entityManager.createNamedQuery("itemsByOrder", OrderItemEntity.class)
          .setParameter("orderEntity", orderEntity).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
