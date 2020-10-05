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

  /**
   * Creates the address entity using the given AddressEntity.
   *
   * @param address contains the address details.
   * @return AddressEntity object.
   */
  public AddressEntity saveAddress(final AddressEntity address) {
    entityManager.persist(address);
    return address;
  }

  /**
   * This method fetches all the addresses of a given customer.
   *
   * @param customer whose detals to be fetched.
   * @return List of CustomerAddressEntity type object.
   */
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


  /**
   * This method fetches the address from Database based on address UUID.
   *
   * @param addressId UUID of the address to be fetched.
   * @return AddressEntity
   */
  public AddressEntity getAddressByAddressId(final String addressId) {
    try {
      AddressEntity address = entityManager.createNamedQuery("addressById", AddressEntity.class)
          .setParameter("addressId", addressId).getSingleResult();
      return address;
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Deletes the given address entity.
   *
   * @param address Address to delete from database.
   * @return AddressEntity object.
   */
  public AddressEntity deleteAddress(final AddressEntity address) {
    entityManager.remove(address);
    return address;
  }

}
