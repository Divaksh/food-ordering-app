package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * CustomerAddressEntity class contains all the attributes to be mapped to all the fields in
 * 'customer_address' table in the database
 */
@Entity
@Table(name = "customer_address")
@NamedQueries({
    @NamedQuery(name = "getAllCustomerAddressByCustomer", query = "SELECT c from CustomerAddressEntity c where c.customer = :customer_entity"),
    @NamedQuery(name = "getCustomerAddressByAddress", query = "SELECT c from CustomerAddressEntity c where c.address = :address_entity"),
    @NamedQuery(name = "getAddressesByCustomer", query = "SELECT cae FROM CustomerAddressEntity cae WHERE cae.customer = :customer"),
    @NamedQuery(name = "customerAddressByAddress", query = "SELECT cae FROM CustomerAddressEntity cae WHERE cae.address = :address")
})

public class CustomerAddressEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id")
  private AddressEntity address;

  public CustomerAddressEntity() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CustomerEntity getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
  }

  public AddressEntity getAddress() {
    return address;
  }

  public void setAddress(AddressEntity address) {
    this.address = address;
  }

}
