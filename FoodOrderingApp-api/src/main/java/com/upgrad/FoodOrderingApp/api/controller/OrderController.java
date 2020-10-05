package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.api.model.OrderList;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddress;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddressState;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.OrderListCustomer;
import com.upgrad.FoodOrderingApp.api.model.OrderListPayment;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private AddressService addressService;

  @Autowired
  private RestaurantService restaurantService;


  /**
   * This API endpoint gets coupon details by coupon name
   *
   * @param authorization Bearer <access-token>
   * @param couponName    Name of the coupon whose details are required.
   * @return CouponDetailsResponse
   * @throws AuthorizationFailedException If authorization is not valid.
   * @throws CouponNotFoundException      If coupon name doesn't exist in database
   */
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

  /**
   * Fetch the orders of the customer.
   *
   * @param authorization Bearer <access-token>
   * @return CustomerOrderResponse
   * @throws AuthorizationFailedException If authorization is not valid.
   */
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

  /**
   * To save the customer order if it is valid.
   *
   * @param authorization    Bearer <access-token>
   * @param saveOrderRequest Contains the order details.
   * @return SaveOrderResponse
   * @throws AuthorizationFailedException   If authorization is not valid.
   * @throws CouponNotFoundException        if the coupon id entered is not valid.
   * @throws AddressNotFoundException       if the address id entered doesn't belong to customer.
   * @throws PaymentMethodNotFoundException if the payment id entered isn't available in db.
   * @throws RestaurantNotFoundException    if the restaurant id entered isn't available in db.
   * @throws ItemNotFoundException          if the item id entered isn't available in db.
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SaveOrderResponse> saveOrder(
      @RequestBody(required = false) final SaveOrderRequest saveOrderRequest,
      @RequestHeader("authorization") String authorization)
      throws AuthorizationFailedException, PaymentMethodNotFoundException, RestaurantNotFoundException,
      ItemNotFoundException, CouponNotFoundException, AddressNotFoundException {

    String accessToken = authorization.split("Bearer ")[1];
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    final OrderEntity orderEntity = new OrderEntity();
    orderEntity.setUuid(UUID.randomUUID().toString());
    orderEntity
        .setCoupon(orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString()));
    orderEntity
        .setPayment(paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString()));
    orderEntity.setCustomer(customerEntity);
    orderEntity.setAddress(
        addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity));
    orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
    orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
    orderEntity.setRestaurant(
        restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString()));
    orderEntity.setDate(new Date());
    OrderEntity savedOrderEntity = orderService.saveOrder(orderEntity);

    for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
      OrderItemEntity orderItemEntity = new OrderItemEntity();
      orderItemEntity.setOrderId(savedOrderEntity);
      orderItemEntity.setItemId(itemService.getItemByUUID(itemQuantity.getItemId().toString()));
      orderItemEntity.setQuantity(itemQuantity.getQuantity());
      orderItemEntity.setPrice(itemQuantity.getPrice());
      orderService.saveOrderItem(orderItemEntity);
    }

    SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
        .id(savedOrderEntity.getUuid()).status("ORDER SUCCESSFULLY PLACED");
    return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
  }
}
