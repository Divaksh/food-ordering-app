package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderDao {

  @PersistenceContext
  private EntityManager entityManager;

  public CouponEntity getCouponByName(String couponName) {
    final CouponEntity couponEntity;
    try {
      couponEntity = entityManager.createNamedQuery("couponByCouponName", CouponEntity.class)
          .setParameter("couponName", couponName).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }

    return couponEntity;
  }

  /**
   * Fetches the orders of the customer in a sorted manner with latest order being on the top.
   *
   * @param customerEntity customer whose orders are to be fetched
   * @return list of orders made by customer.
   */
  public List<OrderEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
    try {
      return entityManager.createNamedQuery("ordersByCustomer", OrderEntity.class)
          .setParameter("customer", customerEntity).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Order to be persisted in the database.
   *
   * @param order
   * @return Persisted Order.
   */
  @Transactional
  public OrderEntity createNewOrder(OrderEntity order) {
    entityManager.persist(order);
    return order;
  }

  /**
   * Order item that is to be persisted in the database.
   *
   * @param orderItemEntity
   * @return persisted order item.
   */
  @Transactional
  public OrderItemEntity createNewOrderItem(OrderItemEntity orderItemEntity) {
    entityManager.persist(orderItemEntity);
    return orderItemEntity;
  }

  /**
   * This method fetches and returns all the orders of a restaurant
   *
   * @param restaurant customer whose orders are to be fetched
   * @return list of orders of a restaurant.
   */
  public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurant) {
    try {
      return entityManager.createNamedQuery("ordersByRestaurant", OrderEntity.class)
          .setParameter("restaurant", restaurant).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

}
