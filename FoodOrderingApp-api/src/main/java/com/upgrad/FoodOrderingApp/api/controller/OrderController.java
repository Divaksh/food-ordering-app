package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @Autowired
  private CustomerService customerService;

  // Method that implements the endpoint to get coupon name
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CouponDetailsResponse> getCouponByName(
      @PathVariable(value = "coupon_name") final String couponName,
      @RequestHeader(value = "authorization") final String authorization)
      throws AuthorizationFailedException, CouponNotFoundException {

    String accessToken = authorization.split("Bearer ")[1];
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);

    CouponDetailsResponse couponDetails = new CouponDetailsResponse()
        .id(UUID.fromString(couponEntity.getUuid()))
        .couponName(couponEntity.getCouponName())
        .percent(couponEntity.getPercent());
    return new ResponseEntity<CouponDetailsResponse>(couponDetails, HttpStatus.OK);
  }

}
