package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 * CustomerAddressDao class provides the database access for all the required endpoints inside the
 * customer and address controllers.
 */
@Repository
public class CustomerAddressDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Creates mapping between the customer and the address entity.
   *
   * @param customerAddress Customer and the address to map.
   * @return CustomerAddressEntity object.
   */
  public CustomerAddressEntity saveCustomerAddress(final CustomerAddressEntity customerAddress) {
    entityManager.persist(customerAddress);
    return customerAddress;
  }

  /**
   * fetches the address of a customer using givne address.
   *
   * @param address address to fetch.
   * @return CustomerAddressEntity type object.
   */
  public CustomerAddressEntity getCustomerAddressByAddress(final AddressEntity address) {
    try {
      CustomerAddressEntity customerAddressEntity = entityManager
          .createNamedQuery("customerAddressByAddress", CustomerAddressEntity.class)
          .setParameter("address", address).getSingleResult();
      return customerAddressEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

}