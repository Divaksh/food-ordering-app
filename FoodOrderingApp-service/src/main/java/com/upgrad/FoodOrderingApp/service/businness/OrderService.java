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

  //Service method to get an instance of the Coupon Entity when Coupon name is provided

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

  // Service method to get the customer orders given customer UUID
  public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
    return orderDao.getOrdersByCustomers(customerDao.getCustomerByUUID(customerUUID));
  }

  //Service method to get an instance of the Coupon Entity when Coupon UUID is provided
  public CouponEntity getCouponByCouponId(String uuid) throws CouponNotFoundException {

    CouponEntity coupon = couponDao.getCouponByUUID(uuid);
    if (coupon == null) {
      throw new CouponNotFoundException("CPF-002", "No coupon by this id");
    }

    return coupon;
  }

  // Service Method to save the order
  @Transactional(propagation = Propagation.REQUIRED)
  public OrderEntity saveOrder(OrderEntity order) {
    OrderEntity newOrder = orderDao.createNewOrder(order);
    return newOrder;
  }

  //Service method to save individual order items
  public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
    OrderItemEntity newOrderItemEntity = orderDao.createNewOrderItem(orderItemEntity);
    return newOrderItemEntity;
  }

}
