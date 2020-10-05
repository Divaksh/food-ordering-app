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
import javax.validation.constraints.NotNull;

/**
 * OrderItemEntity class contains all the attributes to be mapped to all the fields in 'order_item'
 * table in the database
 */
@Entity
@Table(name = "order_item")
@NamedQueries({
    @NamedQuery(name = "itemsByOrder", query = "select q from OrderItemEntity q where q.orderId = :orderEntity"),
})

public class OrderItemEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id")
  private OrderEntity orderId;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "item_id")
  private ItemEntity itemId;

  @NotNull
  @Column(name = "quantity")
  private Integer quantity;

  @NotNull
  @Column(name = "price")
  private Integer price;

  public OrderItemEntity() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public OrderEntity getOrderId() {
    return orderId;
  }

  public void setOrderId(OrderEntity orderId) {
    this.orderId = orderId;
  }

  public ItemEntity getItemId() {
    return itemId;
  }

  public void setItemId(ItemEntity itemId) {
    this.itemId = itemId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }
}
