package com.upgrad.FoodOrderingApp.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.provider.BearerAuthDecoder;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private AddressService addressService;

  @CrossOrigin
  @RequestMapping(method = POST, path = "/address", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SaveAddressResponse> saveAddress(
      @RequestHeader("authorization") final String authorization,
      @RequestBody(required = false) final SaveAddressRequest addressRequest)
      throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException {
    // Gets the access-token from the Authorization header (Bearer Token)
    BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
    final String accessToken = bearerAuthDecoder.getAccessToken();

    // Gets customer entity based on access token
    CustomerEntity existingCustomer = customerService.getCustomer(accessToken);

    // Formulates the Address entity from the input request
    AddressEntity address = new AddressEntity();
    address.setFlatBuilNo(addressRequest.getFlatBuildingName());
    address.setLocality(addressRequest.getLocality());
    address.setCity(addressRequest.getCity());
    address.setPincode(addressRequest.getPincode());
    address.setUuid(UUID.randomUUID().toString());
    StateEntity state = addressService.getStateByUUID(addressRequest.getStateUuid());

    // Save address to the database
    AddressEntity savedAddress = addressService.saveAddress(address, state);
    CustomerAddressEntity customerAddress = addressService
        .saveCustomerAddress(existingCustomer, savedAddress);

    // Generate the SaveAddressResponse
    SaveAddressResponse addressResponse = new SaveAddressResponse()
        .id(savedAddress.getUuid())
        .status("ADDRESS SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
  }

  @CrossOrigin
  @RequestMapping(method = GET, path = "/address/customer", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AddressListResponse> getAllAddresses(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {
    // Gets the access-token from the authorization header
    BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
    final String accessToken = bearerAuthDecoder.getAccessToken();
    // Gets customer entity from provided access token
    CustomerEntity existingCustomer = customerService.getCustomer(accessToken);
    // Call to database to fetch all the addresses of the corresponding customer
    List<AddressEntity> addresses = addressService.getAllAddress(existingCustomer);
    Collections.reverse(addresses);
    List<AddressList> addressesList = new LinkedList<>();
    // Formulate the AddressListResponse
    addresses.forEach(address -> {
      AddressListState addressListState = new AddressListState();
      addressListState.setId(UUID.fromString(address.getState().getUuid()));
      addressListState.setStateName(address.getState().getStateName());

      AddressList addressList = new AddressList()
          .id(UUID.fromString(address.getUuid()))
          .flatBuildingName(address.getFlatBuilNo())
          .city(address.getCity())
          .locality(address.getLocality())
          .pincode(address.getPincode())
          .state(addressListState);
      addressesList.add(addressList);
    });

    AddressListResponse addressListResponse = new AddressListResponse().addresses(addressesList);
    return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);

  }

}
