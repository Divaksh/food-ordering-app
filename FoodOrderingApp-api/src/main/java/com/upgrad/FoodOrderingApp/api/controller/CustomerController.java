package com.upgrad.FoodOrderingApp.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordRequest;
import com.upgrad.FoodOrderingApp.api.model.UpdatePasswordResponse;
import com.upgrad.FoodOrderingApp.api.provider.BasicAuthDecoder;
import com.upgrad.FoodOrderingApp.api.provider.BearerAuthDecoder;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
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

  @CrossOrigin
  @RequestMapping(method = POST, path = "/customer/logout", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<LogoutResponse> logout(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {
    // Get the access-token from the authorization header (Bearer token)
    BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
    final String accessToken = bearerAuthDecoder.getAccessToken();
    // Given access-token, logout the corresponding customer
    CustomerAuthEntity loggedOutCustomerAuth = customerService.logout(accessToken);
    // Generate the response of successful logout
    LogoutResponse logoutResponse = new LogoutResponse()
        .id(loggedOutCustomerAuth.getCustomer().getUuid())
        .message("LOGGED OUT SUCCESSFULLY");
    return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
  }

  @CrossOrigin
  @RequestMapping(method = PUT, path = "/customer", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdateCustomerResponse> updateCustomer(
      @RequestHeader("authorization") final String authorization,
      @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest)
      throws UpdateCustomerException, AuthorizationFailedException {
    // Get the access-token from authorization header(Bearer)
    BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
    final String accessToken = bearerAuthDecoder.getAccessToken();
    // Check if the firstname field of request is not empty and accordingly raise an exception
    if (updateCustomerRequest.getFirstName() == null || updateCustomerRequest.getFirstName()
        .isEmpty()) {
      throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
    }
    // Get customer based on access-token
    CustomerEntity customer = customerService.getCustomer(accessToken);
    // Update user/ customer details
    customer.setFirstName(updateCustomerRequest.getFirstName());
    customer.setLastName(updateCustomerRequest.getLastName());
    CustomerEntity updatedCustomer = customerService.updateCustomer(customer);
    // Generate response on successfully updating the customer details
    UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
        .id(updatedCustomer.getUuid())
        .firstName(updatedCustomer.getFirstName())
        .lastName(updatedCustomer.getLastName())
        .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
    return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
  }

  @CrossOrigin
  @RequestMapping(method = PUT, path = "/customer/password", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UpdatePasswordResponse> updatePassword(
      @RequestHeader("authorization") final String authorization,
      @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest)
      throws AuthorizationFailedException, UpdateCustomerException {
    // Get access-token from authorization header
    BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
    final String accessToken = bearerAuthDecoder.getAccessToken();
    // get the old and new passwords from the request body
    final String oldPassword = updatePasswordRequest.getOldPassword();
    final String newPassword = updatePasswordRequest.getNewPassword();
    // Check if both the input parameters aren't empty, and accordingly raise an exception
    if (oldPassword == "" || newPassword == "") {
      throw new UpdateCustomerException("UCR-003", "No field should be empty");
    }
    // Get customer based on access-token
    CustomerEntity customer = customerService.getCustomer(accessToken);
    // Update customer's password
    CustomerEntity updatedCustomer = customerService
        .updateCustomerPassword(oldPassword, newPassword, customer);
    // Generate response if password successfully updated
    UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
        .id(updatedCustomer.getUuid())
        .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
    return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
  }

  // Verifies if the input fields aren't empty
  private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
    return !customer.getFirstName().isEmpty() &&
        !customer.getContactNumber().isEmpty() &&
        !customer.getEmail().isEmpty() &&
        !customer.getPassword().isEmpty();
  }

}
