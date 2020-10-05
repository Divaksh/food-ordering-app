package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerAuthDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method stores authorization access token in the database
   *
   * @param customerAuthEntity the CustomerAuthEntity object from which new authorization will be
   *                           created
   */
  public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity customerAuthEntity) {
    entityManager.persist(customerAuthEntity);
    return customerAuthEntity;
  }

  /**
   * @param accessToken access-token obtained during successful login.
   * @param accessToken the access token which will be searched in database to find the customer.
   * @return CustomerAuthEntity object if given access token exists in the database.
   */
  public CustomerAuthEntity findCustomerAuthByAccessToken(final String accessToken) {
    final CustomerAuthEntity loggedInCustomerAuth;
    try {
      loggedInCustomerAuth = entityManager
          .createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class)
          .setParameter("accessToken", accessToken).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
    return loggedInCustomerAuth;
  }

  /**
   * This method updates the customers logout time in the database.
   *
   * @param customerAuthEntity CustomerAuthEntity object to update.
   */
  public CustomerAuthEntity update(CustomerAuthEntity customerAuthEntity) {
    entityManager.merge(customerAuthEntity);
    return customerAuthEntity;
  }
}