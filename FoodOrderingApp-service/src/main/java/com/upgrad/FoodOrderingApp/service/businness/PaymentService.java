package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  @Autowired
  private PaymentDao paymentDao;

  /**
   * This method gets the payment record from database based on the UUID.
   *
   * @param paymentId UUID of the payment that is to be fetched
   * @return the payment entity given the payment id
   */
  public PaymentEntity getPaymentByUUID(String paymentId) throws PaymentMethodNotFoundException {
    PaymentEntity paymentEntity = paymentDao.getPayment(paymentId);
    if (paymentEntity == null) {
      throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
    } else {
      return paymentEntity;
    }
  }

  /**
   * This method gets all the payment methods
   *
   * @return all the payment methods
   */
  public List<PaymentEntity> getAllPaymentMethods() {
    List<PaymentEntity> paymentEntities = paymentDao.getPaymentMethods();
    return paymentEntities;

  }
}
