package com.upgrad.FoodOrderingApp.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
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


  // Verifies if the input fields aren't empty
  private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
    return !customer.getFirstName().isEmpty() &&
        !customer.getContactNumber().isEmpty() &&
        !customer.getEmail().isEmpty() &&
        !customer.getPassword().isEmpty();
  }

}
