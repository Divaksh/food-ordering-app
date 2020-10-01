package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository

public class CouponDao {

  @PersistenceContext
  private EntityManager entityManager;

  //Method to get the Coupon from the database given coupon name
  public CouponEntity getCouponByCouponName(String couponName) {
    try {
      CouponEntity couponEntity = entityManager
          .createNamedQuery("couponByCouponName", CouponEntity.class)
          .setParameter("couponName", couponName).getSingleResult();
      return couponEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }


}
