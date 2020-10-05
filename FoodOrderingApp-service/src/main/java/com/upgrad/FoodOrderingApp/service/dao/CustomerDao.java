package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method helps finds the customer by using contact number.
   *
   * @param contactNumber to find the customer is already registered with this number
   * @return CustomerEntity if the contact number exists in the database
   */
  public CustomerEntity findByContactNumber(String contactNumber) {
    try {
      CustomerEntity customer = entityManager
          .createNamedQuery("customerByContactNumber", CustomerEntity.class)
          .setParameter("contactNumber", contactNumber)
          .getSingleResult();
      return customer;
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * This method saves the details of the new customer in database.
   *
   * @param customer for creating new customer.
   * @return CustomerEntity object.
   */
  public CustomerEntity createCustomer(CustomerEntity customer) {
    entityManager.persist(customer);
    return customer;
  }

  /**
   * This method updates the customer details in the database.
   *
   * @param customer CustomerEntity object to update.
   * @return Updated CustomerEntity object.
   */
  public CustomerEntity updateCustomer(CustomerEntity customer) {
    entityManager.merge(customer);
    return customer;
  }

  public CustomerEntity updatePassword(CustomerEntity customer) {
    entityManager.merge(customer);
    return customer;
  }

  public CustomerEntity getCustomerByUUID(String uuid) {
    try {
      return entityManager.createNamedQuery("customerByUUID", CustomerEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}