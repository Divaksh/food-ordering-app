package com.upgrad.FoodOrderingApp.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.provider.BasicAuthDecoder;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @CrossOrigin
  @RequestMapping(method = POST, path = "/customer/signup", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupCustomerResponse> signup(
      @RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest)
      throws SignUpRestrictedException {
    // Create instance of customer entity and populate the request body
    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setFirstName(signupCustomerRequest.getFirstName());
    customerEntity.setLastName(signupCustomerRequest.getLastName());
    customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
    customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
    customerEntity.setPassword(signupCustomerRequest.getPassword());
    customerEntity.setUuid(UUID.randomUUID().toString());
    // If all required fields not populated, raise an exception
    if (!fieldsComplete(customerEntity)) {
      throw new SignUpRestrictedException("SGR-005",
          "Except last name all fields should be filled");
    }
    // If fields are valid, save the customer details
    CustomerEntity createdCustomer = customerService.saveCustomer(customerEntity);
    // Generate success response on successfully saving details to the DB
    SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse()
        .id(createdCustomer.getUuid())
        .status("CUSTOMER SUCCESSFULLY REGISTERED");

    return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);

  }

  @CrossOrigin
  @RequestMapping(method = POST, path = "/customer/login", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<LoginResponse> login(
      @RequestHeader("authorization") final String authorization)
      throws AuthenticationFailedException {
    // Gets username and password from the Authorization header (Basic) - which is base64 encoded
    BasicAuthDecoder basicAuthDecoder = new BasicAuthDecoder(authorization);
    // Validate if the user credentials are valid
    CustomerAuthEntity authorizedCustomer = customerService
        .authenticate(basicAuthDecoder.getUsername(), basicAuthDecoder.getPassword());
    // If Valid, get the customer entity
    CustomerEntity customer = authorizedCustomer.getCustomer();
    // Formulate the response for successful login
    LoginResponse loginResponse = new LoginResponse()
        .id(customer.getUuid())
        .contactNumber(customer.getContactNumber())
        .emailAddress(customer.getEmail())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .message("LOGGED IN SUCCESSFULLY");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("access-token", authorizedCustomer.getAccessToken());
    return new ResponseEntity<LoginResponse>(loginResponse, httpHeaders, HttpStatus.OK);
  }


  // Verifies if the input fields aren't empty
  private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
    return !customer.getFirstName().isEmpty() &&
        !customer.getContactNumber().isEmpty() &&
        !customer.getEmail().isEmpty() &&
        !customer.getPassword().isEmpty();
  }

}
