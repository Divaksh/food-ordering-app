package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;


@Repository
public class AddressDao {

  @PersistenceContext
  private EntityManager entityManager;

  public AddressEntity saveAddress(final AddressEntity address) {
    entityManager.persist(address);
    return address;
  }


  public List<CustomerAddressEntity> getAddressesByCustomer(CustomerEntity customer) {
    try {
      List<CustomerAddressEntity> customerAddressEntities = entityManager
          .createNamedQuery("getAddressesByCustomer", CustomerAddressEntity.class)
          .setParameter("customer", customer).getResultList();
      return customerAddressEntities;
    } catch (NoResultException nre) {
      return null;
    }
  }

}
