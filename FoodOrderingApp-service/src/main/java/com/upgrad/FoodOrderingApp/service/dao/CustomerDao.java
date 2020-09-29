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

  public CustomerEntity createCustomer(CustomerEntity customer) {
    entityManager.persist(customer);
    return customer;
  }

  public CustomerEntity updateCustomer(CustomerEntity customer) {
    entityManager.merge(customer);
    return customer;
  }

}