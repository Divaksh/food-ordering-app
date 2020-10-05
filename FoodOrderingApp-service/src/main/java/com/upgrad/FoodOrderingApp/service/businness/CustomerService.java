package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

  @Autowired
  private CustomerDao customerDao;

  @Autowired
  private PasswordCryptographyProvider passwordCryptographyProvider;

  @Autowired
  private CustomerAuthDao customerAuthDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity saveCustomer(CustomerEntity newCustomer) throws SignUpRestrictedException {
    // Finds customer based on contact number
    CustomerEntity existingCustomer = customerDao
        .findByContactNumber(newCustomer.getContactNumber());
    // Checks if customer with given contact number exists
    if (existingCustomer != null) {
      throw new SignUpRestrictedException("SGR-001",
          "This contact number is already registered! Try other contact number");
    }
    // Verifies if all input fields are provided
    if (!fieldsComplete(newCustomer)) {
      throw new SignUpRestrictedException("SGR-005",
          "Except last name all fields should be filled");
    }
    // Verifies the email address
    if (!validEmailAddress(newCustomer.getEmail())) {
      throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
    }
    // Validates the contact number
    if (!validContactNumber(newCustomer.getContactNumber())) {
      throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
    }
    // Validates the strength of the given password
    if (!validPassword(newCustomer.getPassword())) {
      throw new SignUpRestrictedException("SGR-004", "Weak password!");
    }
    // Provided password is encrypted using the passwordCryptographyProvider
    encryptPassword(newCustomer);
    // Save customer details in the database
    return customerDao.createCustomer(newCustomer);

  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity authenticate(final String username, final String password)
      throws AuthenticationFailedException {
    // Check if the given username exists in the database
    CustomerEntity registeredCustomer = customerDao.findByContactNumber(username);
    if (registeredCustomer == null) {
      throw new AuthenticationFailedException("ATH-001",
          "This contact number has not been registered!");
    }
    final String encryptedPassword = PasswordCryptographyProvider
        .encrypt(password, registeredCustomer.getSalt());
    // Verify if the old and new passwords are the same
    if (registeredCustomer.getPassword().equals(encryptedPassword)) {
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
      CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
      customerAuthEntity.setUuid(UUID.randomUUID().toString());
      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(8);
      customerAuthEntity.setAccessToken(
          jwtTokenProvider.generateToken(registeredCustomer.getUuid(), now, expiresAt));
      customerAuthEntity.setExpiresAt(expiresAt);
      customerAuthEntity.setCustomer(registeredCustomer);
      customerAuthEntity.setLoginAt(now);
      CustomerAuthEntity authCustomer = customerAuthDao.createCustomerAuth(customerAuthEntity);
      return authCustomer;
    } else {
      throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
    final ZonedDateTime now;
    // finds customer based on access token
    CustomerAuthEntity loggedInCustomerAuth = customerAuthDao
        .findCustomerAuthByAccessToken(accessToken);
    // Check if the customer is logged in
    if (loggedInCustomerAuth == null) {
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    }
    // Check if the customer has already logged out
    if (loggedInCustomerAuth.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "Customer is logged out. Log in again to access this endpoint.");
    }
    // Check if the customer's session has got expired
    now = ZonedDateTime.now(ZoneId.systemDefault());
    if (loggedInCustomerAuth.getExpiresAt().isBefore(now) || loggedInCustomerAuth.getExpiresAt()
        .isEqual(now)) {
      throw new AuthorizationFailedException("ATHR-003",
          "Your session is expired. Log in again to access this endpoint.");
    }
    loggedInCustomerAuth.setLogoutAt(ZonedDateTime.now(ZoneId.systemDefault()));
    CustomerAuthEntity loggedOutCustomerAuth = customerAuthDao.update(loggedInCustomerAuth);
    return loggedOutCustomerAuth;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity updateCustomer(final CustomerEntity customer)
      throws AuthorizationFailedException, UpdateCustomerException {
    // update customer details in the database
    CustomerEntity updatedCustomer = customerDao.updateCustomer(customer);
    return updatedCustomer;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
    // Get the customer details based on access token
    CustomerAuthEntity customerAuth = customerAuthDao.findCustomerAuthByAccessToken(accessToken);
    final ZonedDateTime now;
    // Validates if customer is logged in
    if (customerAuth == null) {
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    }
    // Validates if customer has logged out
    if (customerAuth.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "Customer is logged out. Log in again to access this endpoint.");
    }
    now = ZonedDateTime.now(ZoneId.systemDefault());
    // Verifies if customer session has expired
    if (customerAuth.getExpiresAt().isBefore(now) || customerAuth.getExpiresAt().isEqual(now)) {
      throw new AuthorizationFailedException("ATHR-003",
          "Your session is expired. Log in again to access this endpoint.");
    }
    return customerAuth.getCustomer();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword,
      final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {
    // Update customer password in the database
    if (!validPassword(newPassword)) {
      throw new UpdateCustomerException("UCR-001", "Weak password!");
    }

    String encryptedOldPassword = PasswordCryptographyProvider
        .encrypt(oldPassword, customer.getSalt());
    if (!encryptedOldPassword.equals(customer.getPassword())) {
      throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
    }

    customer.setPassword(newPassword);
    encryptPassword(customer);
    CustomerEntity updatedCustomer = customerDao.updatePassword(customer);
    return updatedCustomer;
  }

  // Verify if all fields are provided with values
  private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
    return !customer.getFirstName().isEmpty() &&
        !customer.getContactNumber().isEmpty() &&
        !customer.getEmail().isEmpty() &&
        !customer.getPassword().isEmpty();
  }

  // Verify email address
  private boolean validEmailAddress(String email) {
    String regex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  // Verify validity of contact number
  private boolean validContactNumber(String contact) {
    String regex = "[0-9]{10}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(contact);
    return matcher.find() && matcher.group().equals(contact);
  }

  // Verify validity of password
  private boolean validPassword(String password) {
    String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(password);
    return m.matches();
  }

  // Encrypts the provided password
  private void encryptPassword(final CustomerEntity newCustomer) {
    String password = newCustomer.getPassword();
    final String[] encryptedData = passwordCryptographyProvider.encrypt(password);
    newCustomer.setSalt(encryptedData[0]);
    newCustomer.setPassword(encryptedData[1]);
  }
}
