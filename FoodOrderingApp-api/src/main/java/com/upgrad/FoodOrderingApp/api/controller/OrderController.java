package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.api.model.OrderList;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddress;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddressState;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.OrderListCustomer;
import com.upgrad.FoodOrderingApp.api.model.OrderListPayment;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import java.math.BigDecimal;
import java.util.List;
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

  @Autowired
  private ItemService itemService;

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

  // Method to implement the endpoint to get past orders of customer
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CustomerOrderResponse> getCustomerOrders(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {
    String accessToken = authorization.split("Bearer ")[1];
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    // Get all orders by customer
    List<OrderEntity> orderEntityList = orderService.getOrdersByCustomers(customerEntity.getUuid());

    // Create response
    CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();

    for (OrderEntity orderEntity : orderEntityList) {

      OrderListCoupon orderListCoupon = new OrderListCoupon()
          .id(UUID.fromString(orderEntity.getCoupon().getUuid()))
          .couponName(orderEntity.getCoupon().getCouponName())
          .percent(orderEntity.getCoupon().getPercent());

      OrderListPayment orderListPayment = new OrderListPayment()
          .id(UUID.fromString(orderEntity.getPayment().getUuid()))
          .paymentName(orderEntity.getPayment().getPaymentName());

      OrderListCustomer orderListCustomer = new OrderListCustomer()
          .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
          .firstName(orderEntity.getCustomer().getFirstName())
          .lastName(orderEntity.getCustomer().getLastName())
          .emailAddress(orderEntity.getCustomer().getEmail())
          .contactNumber(orderEntity.getCustomer().getContactNumber());

      OrderListAddressState orderListAddressState = new OrderListAddressState()
          .id(UUID.fromString(orderEntity.getAddress().getState().getUuid()))
          .stateName(orderEntity.getAddress().getState().getStateName());

      OrderListAddress orderListAddress = new OrderListAddress()
          .id(UUID.fromString(orderEntity.getAddress().getUuid()))
          .flatBuildingName(orderEntity.getAddress().getFlatBuilNo())
          .locality(orderEntity.getAddress().getLocality())
          .city(orderEntity.getAddress().getCity())
          .pincode(orderEntity.getAddress().getPincode())
          .state(orderListAddressState);

      OrderList orderList = new OrderList()
          .id(UUID.fromString(orderEntity.getUuid()))
          .bill(new BigDecimal(orderEntity.getBill()))
          .coupon(orderListCoupon)
          .discount(new BigDecimal(orderEntity.getDiscount()))
          .date(orderEntity.getDate().toString())
          .payment(orderListPayment)
          .customer(orderListCustomer)
          .address(orderListAddress);

      for (OrderItemEntity orderItemEntity : itemService.getItemsByOrder(orderEntity)) {

        ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
            .id(UUID.fromString(orderItemEntity.getItemId().getUuid()))
            .itemName(orderItemEntity.getItemId().getItemName())
            .itemPrice(orderItemEntity.getItemId().getPrice())
            .type(ItemQuantityResponseItem.TypeEnum
                .fromValue(orderItemEntity.getItemId().getType().getValue()));

        ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
            .item(itemQuantityResponseItem)
            .quantity(orderItemEntity.getQuantity())
            .price(orderItemEntity.getPrice());

        orderList.addItemQuantitiesItem(itemQuantityResponse);
      }

      customerOrderResponse.addOrdersItem(orderList);
    }

    return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
  }
}
