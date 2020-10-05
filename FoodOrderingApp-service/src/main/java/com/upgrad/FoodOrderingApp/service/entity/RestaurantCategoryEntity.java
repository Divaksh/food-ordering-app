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
import javax.persistence.Table;

@Entity
@Table(name = "restaurant_category")
public class RestaurantCategoryEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "restaurant_id")
  private RestaurantEntity restaurantId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  private CategoryEntity categoryId;

  public RestaurantCategoryEntity() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RestaurantEntity getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(RestaurantEntity restaurantId) {
    this.restaurantId = restaurantId;
  }

  public CategoryEntity getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(CategoryEntity categoryId) {
    this.categoryId = categoryId;
  }
}
