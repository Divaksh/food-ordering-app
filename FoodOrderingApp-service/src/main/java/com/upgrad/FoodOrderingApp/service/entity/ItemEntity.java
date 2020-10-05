package com.upgrad.FoodOrderingApp.service.entity;

import com.upgrad.FoodOrderingApp.service.common.ItemType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ItemEntity class contains all the attributes to be mapped to all the fields in 'item' table in
 * the database
 */
@Entity
@Table(name = "item")
@NamedQueries({
    @NamedQuery(name = "itemById", query = "select i from ItemEntity i where i.uuid = :itemId"),
    @NamedQuery(name = "itemByUUID", query = "select i from ItemEntity i where i.uuid = :uuid"),
})

public class ItemEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "uuid")
  @Size(max = 200)
  @NotNull
  private String uuid;

  @Column(name = "item_name")
  @Size(max = 30)
  @NotNull
  private String itemName;

  @NotNull
  @Column(name = "price")
  private Integer price;

  @Column(name = "type")
  @Size(max = 10)
  @NotNull
  private ItemType type;

  @ManyToMany
  @JoinTable(name = "restaurant_item", joinColumns = @JoinColumn(name = "item_id"),
      inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
  private List<RestaurantEntity> restaurants = new ArrayList<>();

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItemEntity> orderItem = new ArrayList<>();

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CategoryItemEntity> categoryItem = new ArrayList<>();

  @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<RestaurantItemEntity> restaurantItem = new ArrayList<>();

  public ItemEntity() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public ItemType getType() {
    return type;
  }

  public void setType(ItemType type) {
    this.type = type;
  }

  public List<OrderItemEntity> getOrderItem() {
    return orderItem;
  }

  public void setOrderItem(List<OrderItemEntity> orderItem) {
    this.orderItem = orderItem;
  }

  public List<CategoryItemEntity> getCategoryItem() {
    return categoryItem;
  }

  public void setCategoryItem(List<CategoryItemEntity> categoryItem) {
    this.categoryItem = categoryItem;
  }

  public List<RestaurantEntity> getRestaurants() {
    return restaurants;
  }

  public void setRestaurants(List<RestaurantEntity> restaurants) {
    this.restaurants = restaurants;
  }

  public List<RestaurantItemEntity> getRestaurantItem() {
    return restaurantItem;
  }

  public void setRestaurantItem(List<RestaurantItemEntity> restaurantItem) {
    this.restaurantItem = restaurantItem;
  }

}
