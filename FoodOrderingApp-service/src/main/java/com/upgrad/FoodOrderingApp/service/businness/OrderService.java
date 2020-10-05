package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  @Autowired
  private CouponDao couponDao;

  @Autowired
  private OrderDao orderDao;

  @Autowired
  private CustomerDao customerDao;

  /**
   * This method contains business logic to get coupon details by coupon name.
   *
   * @param couponName
   * @return instance of the Coupon Entity when Coupon name is provided
   * @throws CouponNotFoundException if coupon with that name doesn't exist in database.
   */
  public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {

    if (couponName.equals("")) {
      throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
    }

    CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);

    if (couponEntity == null) {
      throw new CouponNotFoundException("CPF-001", "No coupon by this name");
    }

    return couponEntity;
  }

  /**
   * Fetches the orders of the customer in a sorted manner with latest order being on the top.
   *
   * @param customerUUID customer whose orders are to be fetched.
   * @return list of orders made by customer
   */
  public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
    return orderDao.getOrdersByCustomers(customerDao.getCustomerByUUID(customerUUID));
  }

  /**
   * This method contains business logic to get coupon details by coupon id.
   *
   * @param couponUUID
   * @return instance of the Coupon Entity
   * @throws CouponNotFoundException if coupon with that id doesn't exist in database.
   */
  public CouponEntity getCouponByCouponId(String couponUUID) throws CouponNotFoundException {

    CouponEntity coupon = couponDao.getCouponByUUID(couponUUID);
    if (coupon == null) {
      throw new CouponNotFoundException("CPF-002", "No coupon by this id");
    }

    return coupon;
  }

  /**
   * Persists the order in the database.
   *
   * @param order Order to be persisted.
   * @return Persisted order.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public OrderEntity saveOrder(OrderEntity order) {
    OrderEntity newOrder = orderDao.createNewOrder(order);
    return newOrder;
  }

  /**
   * Persists the Order Item.
   *
   * @param orderItemEntity Order Item to be persisted.
   * @return persisted order item.
   */
  public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
    OrderItemEntity newOrderItemEntity = orderDao.createNewOrderItem(orderItemEntity);
    return newOrderItemEntity;
  }

}
