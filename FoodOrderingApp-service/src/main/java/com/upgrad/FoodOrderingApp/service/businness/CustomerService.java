package com.upgrad.FoodOrderingApp.service.businness;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerService {

  @Autowired
  private CustomerDao customerDao;

  @Autowired
  private PasswordCryptographyProvider passwordCryptographyProvider;

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity saveCustomer(CustomerEntity newCustomer) throws SignUpRestrictedException {
    // Finds customer based on contact number
    CustomerEntity existingCustomer = customerDao.findByContactNumber(newCustomer.getContactNumber());
    // Checks if customer with given contact number exists
    if (existingCustomer != null) {
      throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
    }
    // Verifies if all input fields are provided
    if (!fieldsComplete(newCustomer)) {
      throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
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


  // Verify if all fields are provided with values
  private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
    if ( customer.getFirstName().isEmpty()||
        customer.getContactNumber().isEmpty()||
        customer.getEmail().isEmpty() ||
        customer.getPassword().isEmpty() )
      return false;
    else
      return true;
  }

  // Verify email address
  private boolean validEmailAddress(String email) {
    String regex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher= pattern.matcher(email);
    return  matcher.matches();
  }

  // Verify validity of contact number
  private boolean validContactNumber(String contact) {
    String regex = "[0-9]{10}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(contact);
    if (matcher.find() && matcher.group().equals(contact))
      return true;
    return false;
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
