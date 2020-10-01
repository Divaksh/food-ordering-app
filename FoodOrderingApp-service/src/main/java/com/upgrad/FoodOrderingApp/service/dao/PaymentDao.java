package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao {

  @PersistenceContext
  private EntityManager entityManager;

  public PaymentEntity getPayment(String paymentId) {
    try {
      PaymentEntity paymentEntity = entityManager
          .createNamedQuery("paymentsByPaymentId", PaymentEntity.class)
          .setParameter("paymentId", paymentId)
          .getSingleResult();
      return paymentEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

  public List<PaymentEntity> getPaymentMethods() {
    List<PaymentEntity> paymentEntities = entityManager
        .createNamedQuery("getPaymentMethods", PaymentEntity.class)
        .getResultList();
    return paymentEntities;
  }
}
