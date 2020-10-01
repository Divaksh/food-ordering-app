package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerAddressDao {

  @PersistenceContext
  private EntityManager entityManager;

  public CustomerAddressEntity saveCustomerAddress(final CustomerAddressEntity cutomerAddress) {
    entityManager.persist(cutomerAddress);
    return cutomerAddress;
  }

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