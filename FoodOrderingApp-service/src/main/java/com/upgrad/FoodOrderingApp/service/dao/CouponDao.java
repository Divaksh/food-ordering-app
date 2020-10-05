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

  /**
   * This method fetches CouponEntity from database based on the coupon name.
   *
   * @param couponName
   * @return CouponEntity or null if there is no coupon in database by given name.
   */
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

  /**
   * This method fetches CouponEntity from database based on the coupon id.
   *
   * @param couponId
   * @return CouponEntity or null if there is no coupon in database by given id.
   */
  public CouponEntity getCouponByUUID(String couponId) {
    final CouponEntity couponEntity;
    try {
      couponEntity = entityManager.createNamedQuery("couponByUUID", CouponEntity.class)
          .setParameter("couponId", couponId)
          .getSingleResult();
      return couponEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

}
